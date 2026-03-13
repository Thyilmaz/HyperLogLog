# Büyük Veri Analitiğinde Olasılıksal Veri Yapıları: HyperLogLog (HLL) Tasarımı

Bu proje, devasa veri setlerinde tekil eleman sayısını (cardinality) çok düşük bellek kullanımıyla tahmin etmek için kullanılan **HyperLogLog** algoritmasının sıfırdan Java gerçeklemesini içerir.

## 📌 Proje Amacı
"Cardinality Estimation" problemini çözmek için geliştirilen bu uygulama; veriyi hashleme, kovalara ayırma (bucketing) ve ardışık sıfır sayılarını (trailing/leading zeros) takip ederek istatistiksel bir tahmin yürütür.

## 🛠 Teknik Bileşenler

* **Yüksek Kaliteli Hash:** Verilerin homojen dağılımı için `Google Guava` kütüphanesi üzerinden **MurmurHash3_128** kullanılmıştır.
* **Bucketing (Kovalama):** Hash değerinin ilk `p` biti kullanılarak veriler $m = 2^p$ adet kovaya dağıtılmıştır.
* **Register Yapısı:** Her kova, o kovaya düşen en uzun ardışık sıfır serisini (`max(rho)`) saklayan bir register görevi görür.
* **Harmonik Ortalama:** Tahminin uç değerlerden (outliers) etkilenmemesi için aritmetik ortalama yerine Harmonik Ortalama formülü entegre edilmiştir.
* **Düzeltme Faktörleri:** Küçük veri setleri için **Linear Counting** düzeltmesi uygulanarak doğruluk artırılmıştır.

## 📈 Teorik Hata Analizi
Algoritmanın standart hata sınırı matematiksel olarak şu şekilde ifade edilir:
$$\text{Standart Hata} \approx \frac{1.04}{\sqrt{m}}$$

Projemizde $p=12$ ($m=4096$ kova) kullanıldığında teorik hata sınırı **%1.63**'tür. Yapılan testlerde elde edilen **%0.88**'lik hata payı, tasarımın teorik sınırlarla uyumlu olduğunu göstermektedir.

[Image of HyperLogLog cardinality estimation error rate graph]

## 💻 Kurulum ve Çalıştırma

### Gereksinimler
* Java 25 (OpenJDK)
* Maven

### Adımlar
1.  Depoyu klonlayın:
    ```bash
    git clone [https://github.com/kullaniciadi/hll-cardinality-estimator.git](https://github.com/kullaniciadi/hll-cardinality-estimator.git)
    ```
2.  Bağımlılıkları yükleyin ve derleyin:
    ```bash
    mvn clean install
    ```
3.  Uygulamayı çalıştırın:
    ```bash
    java -cp target/classes:$(mvn dependency:build-classpath | grep -v '\[INFO\]') Main
    ```

## 🎥 Sunum Detayları
* **Geliştirme Ortamı:** IntelliJ IDEA & Maven
* **Dil Modeli:** Gemini / Claude (Agentic Kodlama Yaklaşımı)
* **Temel Mantık:** Bellekten tasarruf etmek için verinin kendisini değil, hash değerindeki bit desenlerini saklama.
* **Özellik:** `merge()` metodu sayesinde iki farklı HLL yapısı kayıpsız birleştirilebilir.

---
**Geliştirici:** [Adın Soyadın]  
**Ders:** Büyük Veri Analitiği
