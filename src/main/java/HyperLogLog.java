import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class HyperLogLog {
    private final int p; // Hassasiyet (bit sayısı)
    private final int m; // Kova sayısı (2^p)
    private final byte[] registers;
    private final double alphaM;

    public HyperLogLog(int p) {
        this.p = p;
        this.m = 1 << p;
        this.registers = new byte[m];

        // Alpha sabiti hesaplama (Düzeltme faktörü)
        if (m == 16) alphaM = 0.673;
        else if (m == 32) alphaM = 0.697;
        else if (m == 64) alphaM = 0.709;
        else alphaM = 0.7213 / (1 + 1.079 / m);
    }

    public void add(String item) {
        // 1. Yüksek kaliteli 64-bit Hash (Murmur3)
        long hash = Hashing.murmur3_128().hashString(item, StandardCharsets.UTF_8).asLong();

        // 2. Bucketing (Kovalama): İlk p biti kova indeksi olarak kullan
        int index = (int) (hash >>> (64 - p));

        // 3. Register Güncelleme: Kalan bitlerdeki ilk 1'in konumunu bul
        // (64 - p) bitlik kısımda sağdan ilk 1'e kadar olan 0 sayısı + 1
        long remainingBits = hash << p;
        byte runOfZeros = (byte) (Long.numberOfLeadingZeros(remainingBits) + 1);

        registers[index] = (byte) Math.max(registers[index], runOfZeros);
    }

    public double estimate() {
        // 4. Harmonik Ortalama Formülü
        double sum = 0;
        for (byte r : registers) {
            sum += Math.pow(2, -r);
        }

        double estimate = alphaM * m * m * (1.0 / sum);

        // 5. Küçük Veri Seti Düzeltmesi (Linear Counting)
        if (estimate <= 2.5 * m) {
            int v = 0; // Boş kova sayısı
            for (byte r : registers) if (r == 0) v++;
            if (v > 0) estimate = m * Math.log((double) m / v);
        }
        return estimate;
    }

    public void merge(HyperLogLog other) {
        if (this.p != other.p) throw new IllegalArgumentException("P değerleri aynı olmalı!");
        for (int i = 0; i < m; i++) {
            this.registers[i] = (byte) Math.max(this.registers[i], other.registers[i]);
        }
    }
}