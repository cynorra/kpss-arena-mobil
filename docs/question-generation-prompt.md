# KPSS Arena — Soru Üretim Prompt'u ve Şeması

Bu dosya, `QuestionPool` (Firestore) koleksiyonuna soru eklemek için kullanılacak
sabit format ve AI prompt şablonudur. Uygulama kodu bu alan adlarını birebir
bekliyor (`FirebaseFirestoreSource.getRandomQuestionsFromPool()` →
`DailyTestDto.QuestionDto`), o yüzden `category` değerleri ve zorunlu alan
adları/tipleri **değiştirilmemeli**.

## Kategoriler — gerçek ÖSYM müfredatı

Web'den doğrulanmış kaynaklara göre (aşağıda linkli), KPSS Lisans Genel
Yetenek-Genel Kültür oturumu 120 sorudan oluşur ve **"Muhakeme" resmen ayrı
bir ders değildir** — Sözel Mantık ve Muhakeme (~4 soru) Türkçe testinin,
Sayısal Mantık (~6 soru) Matematik testinin içinde sorulur. Bu yüzden
uygulamanın kategori listesi gerçek 6 derse göre güncellendi
(`res/values/arrays.xml`, `MistakesFragment.java`):

| Kategori (`category`) | Gerçek soru sayısı (120 üzerinden) | Ağırlık |
|---|---|---|
| Türkçe | 30 (4'ü sözel mantık/muhakeme) | %25 |
| Matematik | 30 (6'sı sayısal mantık, geometri dahil) | %25 |
| Tarih | 27 | %22.5 |
| Coğrafya | 18 | %15 |
| Vatandaşlık | 9 | %7.5 |
| Güncel Bilgiler | 6 | %5 |

Soru havuzunu doldururken bu ağırlıkları kullanıyoruz (örn. 400 soruluk bir
havuzda ~100 Türkçe, ~100 Matematik, ~90 Tarih, ~60 Coğrafya, ~30 Vatandaşlık,
~20 Güncel Bilgiler).

## Sabit şema

```json
{
  "text": "Soru metni",
  "optionA": "A şıkkı",
  "optionB": "B şıkkı",
  "optionC": "C şıkkı",
  "optionD": "D şıkkı",
  "correct": "A",
  "category": "Türkçe",
  "topic": "Sözel mantık ve muhakeme",
  "explanation": "Doğru cevabın kısa gerekçesi"
}
```

- `correct`: sadece `"A"`, `"B"`, `"C"` veya `"D"` olabilir.
- `category`: **sadece** şu altı değerden biri olabilir (uygulamanın admin
  panelindeki sabit kategori listesiyle birebir aynı — `res/values/arrays.xml`).
  Bu alan zorunlu, uygulama filtreleme/gösterim için bunu kullanıyor.
  `"Türkçe"`, `"Matematik"`, `"Tarih"`, `"Coğrafya"`, `"Vatandaşlık"`,
  `"Güncel Bilgiler"`
- `topic`: **opsiyonel**, uygulama tarafından okunmuyor/kullanılmıyor —
  sadece bizim konu dağılımını takip edebilmemiz için (aşağıdaki müfredat
  listesindeki alt başlıklardan biri). Sözel/sayısal muhakeme soruları ayrı
  bir kategori DEĞİL, Türkçe/Matematik altında bir `topic` değeridir.
  Firestore'a yazılsa da uygulama kodu bu alanı görmezden gelir, hiçbir
  riski yok.
- `id` alanı **verilmemeli** — içe aktarma sırasında otomatik atanıyor.
- `explanation` boş bırakılmamalı, kısa ve öğretici olmalı.

## Tam müfredat (doğrulanmış kaynaklara göre)

**Türkçe** (sözel mantık/muhakeme dahil)
- Sözcükte anlam (eş/zıt/mecaz anlam, deyim, atasözü)
- Cümlede anlam (anlatım bozuklukları, cümle tamamlama, cümle ilişkileri)
- Paragraf (ana/yardımcı düşünce, paragraf tamamlama-bölme, anlatım teknikleri)
- Ses bilgisi, yazım (imla) kuralları, noktalama işaretleri
- Sözcük yapısı (kök-ek), sözcük türleri (isim, sıfat, zamir, zarf, edat,
  bağlaç, ünlem, fiil)
- Fiilde çatı, fiilimsi, cümlenin öğeleri, cümle türleri
- **Sözel mantık ve muhakeme** (akıl yürütme, çıkarım, mantıksal bağlaçlar,
  sıralama-eşleştirme) — ~30 soruda 4 tanesi bu tipte olmalı

**Matematik** (geometri ve sayısal mantık dahil)
- Temel kavramlar, bölme-bölünebilme, EBOB-EKOK
- Rasyonel/ondalık sayılar, basit eşitsizlikler, mutlak değer
- Üslü-köklü sayılar, çarpanlara ayırma, özdeşlikler
- Oran-orantı
- Problemler (sayı, kesir, yaş, işçi-havuz, hareket, yüzde, kâr-zarar, karışım)
- Kümeler, fonksiyonlar, permütasyon-kombinasyon, olasılık
- Geometri (açılar, üçgenler, çokgenler, çember-daire, katı cisimler, alan-çevre)
- Grafik ve tablo yorumlama
- **Sayısal mantık** (sayı-şekil ilişkileri, örüntüler, kod-şifre problemleri)
  — ~30 soruda 6 tanesi bu tipte olmalı

**Tarih**
- İslamiyet öncesi Türk tarihi
- Türk-İslam tarihi (İslam tarihi, Türk-İslam devletleri)
- Osmanlı tarihi (kuruluş, yükselme, duraklama, gerileme, dağılma; kültür ve
  medeniyet)
- İnkılap tarihi (Trablusgarp/Balkan Savaşları, I. Dünya Savaşı, Kurtuluş
  Savaşı — Mondros, kongreler, cepheler — Atatürk İlke ve İnkılapları,
  Atatürk dönemi dış politika)
- Çağdaş Türk ve Dünya tarihi (çok partili hayata geçiş, II. Dünya Savaşı,
  Soğuk Savaş, günümüze uzanan gelişmeler)

**Coğrafya**
- Türkiye'nin fiziki özellikleri (yer şekilleri, iklim, bitki örtüsü, su
  kaynakları)
- Nüfus, yerleşme, göç
- Tarım, sanayi, ticaret, ulaşım, turizm
- Bölgeler ve bölgesel kalkınma projeleri (GAP, DAP)
- Harita bilgisi ve yorumlama
- Genel/dünya coğrafyası, çevre ve doğal afetler

**Vatandaşlık**
- Temel hukuk kavramları, hukukun kaynakları
- 1982 Anayasası'nın genel esasları, temel hak ve özgürlükler
- Yasama (TBMM, yasama süreci)
- Yürütme (Cumhurbaşkanlığı, bakanlıklar)
- Yargı (Anayasa Mahkemesi, yüksek yargı organları)
- İdare hukuku, yerel yönetimler (il özel idaresi, belediye, köy)
- Uluslararası kuruluşlar (BM, NATO, AB)

**Güncel Bilgiler**
- Türkiye ve dünya gündemi (siyasi, ekonomik, sosyal, bilim-teknoloji, spor,
  kültür-sanat gelişmeleri)
- Güncel önemli kişi/kurum/etkinlikler

## Kopyala-yapıştır AI prompt'u

```
Sen bir KPSS (Kamu Personeli Seçme Sınavı) soru yazarısın. Aşağıdaki kurallara
göre gerçekçi, sınav standardında çoktan seçmeli sorular üret.

KURALLAR:
1. Çıktı SADECE geçerli bir JSON array olacak. Markdown, kod bloğu işareti,
   açıklama metni, başlık YOK — sadece [ ile başlayıp ] ile biten ham JSON.
2. Her soru objesi TAM OLARAK şu alanlara sahip olacak (başka alan ekleme,
   isim değiştirme):
   - "text": string, soru kökü
   - "optionA", "optionB", "optionC", "optionD": string, şıklar
   - "correct": "A" | "B" | "C" | "D" (doğru şıkkın harfi)
   - "category": SADECE şu altı değerden biri: "Türkçe", "Matematik",
     "Tarih", "Coğrafya", "Vatandaşlık", "Güncel Bilgiler"
   - "topic": string, aşağıdaki müfredat listesindeki alt konulardan biri
   - "explanation": string, doğru cevabın 1-3 cümlelik kısa açıklaması
3. Sorular Türkçe, KPSS Lisans/Önlisans/Ortaöğretim Genel Yetenek-Genel
   Kültür seviyesinde, gerçek ÖSYM sınav formatına uygun olacak.
4. Kategoriler arasında şu GERÇEK ÖSYM ağırlığını kullan (120 soruluk resmi
   sınav dağılımı — üreteceğin toplam soru sayısını bu oranlarla dağıt):
   Türkçe %25, Matematik %25, Tarih %22.5, Coğrafya %15, Vatandaşlık %7.5,
   Güncel Bilgiler %5.
5. Türkçe kategorisinde ürettiğin soruların ~%13'ü "Sözel mantık ve muhakeme"
   topic'inde olsun (akıl yürütme, çıkarım, sıralama-eşleştirme). Matematik
   kategorisinde ürettiğin soruların ~%20'si "Sayısal mantık" topic'inde
   olsun (sayı-şekil ilişkisi, örüntü, kod-şifre). Bunlar ayrı bir kategori
   DEĞİL, Türkçe/Matematik içinde bir topic'tir.
6. Aşağıdaki müfredattaki HER alt konudan en az bir soru üret, konular
   arasında mümkün olduğunca dengeli dağıt:

MÜFREDAT:
- Türkçe: sözcükte anlam, cümlede anlam, paragraf, ses bilgisi/yazım/
  noktalama, sözcük yapısı ve türleri, fiilde çatı/fiilimsi, cümlenin
  öğeleri ve türleri, sözel mantık ve muhakeme
- Matematik: temel kavramlar/bölünebilme/EBOB-EKOK, rasyonel sayılar/
  eşitsizlik/mutlak değer, üslü-köklü sayılar/çarpanlara ayırma, oran-
  orantı, problemler (sayı/kesir/yaş/işçi-havuz/hareket/yüzde/kâr-zarar/
  karışım), kümeler/fonksiyonlar/permütasyon-kombinasyon/olasılık,
  geometri, grafik-tablo yorumlama, sayısal mantık
- Tarih: İslamiyet öncesi Türk tarihi, Türk-İslam tarihi, Osmanlı tarihi,
  İnkılap tarihi (Kurtuluş Savaşı, Atatürk İlke ve İnkılapları), Çağdaş
  Türk ve Dünya tarihi
- Coğrafya: fiziki coğrafya, nüfus/yerleşme/göç, tarım/sanayi/ulaşım/
  turizm, bölgesel kalkınma projeleri, harita bilgisi, dünya coğrafyası/
  çevre-afetler
- Vatandaşlık: temel hukuk, 1982 Anayasası ve temel hak/özgürlükler,
  yasama-yürütme-yargı, idare hukuku/yerel yönetim, uluslararası kuruluşlar
- Güncel Bilgiler: Türkiye ve dünya gündemi

7. Zorluk seviyesi kolay/orta/zor karışık olsun.
8. Aynı soruyu veya çok benzer varyasyonlarını tekrar ETME.
9. Her sorunun tek bir doğru cevabı olsun, şıklar birbirine yakın uzunlukta
   ve mantıklı çeldiriciler içersin (belli olmayan/saçma şıklar yazma).

Üretilecek toplam soru sayısı: {SAYI}
(Belirtilmezse 120 üret: gerçek ÖSYM oranlarına göre dağıtılmış.)

Sadece JSON array'i döndür, başka hiçbir şey yazma.
```

## Kullanım

1. Yukarıdaki prompt'u istediğin bir AI'ya (ChatGPT, Gemini, Claude vb.) yapıştır,
   `{SAYI}` yerine kaç soru istediğini yaz (örn. 400 — yukarıdaki oranlarla
   otomatik dağıtılır).
2. Dönen JSON array'i bir `.json` dosyası olarak kaydet ve bana ver
   (veya doğrudan mesaj olarak yapıştır).
3. Ben bu dosyayı Firestore `QuestionPool` koleksiyonuna yükleyecek script'i
   çalıştırırım — format birebir bu şemayla eşleştiği için ekstra dönüştürme
   gerekmez.
4. İleride daha fazla soru eklemek istersen aynı prompt'u tekrar kullan, sadece
   `{SAYI}`'yı değiştir — şema hep aynı kalacağı için sorunsuz eklenir.

## Kaynaklar (kategori/ağırlık araştırması)

- [KPSS Lisans Soru Dağılımı 2026 — dopinghafiza.com](https://www.dopinghafiza.com/ders/kpss-lisans/kpss-lisans-soru-dagilimi-2026-genel-yetenek-ve-genel-kultur)
- [KPSS Lisans Konuları Listesi (2026 Güncel PDF) — kpssegitimi.com](https://kpssegitimi.com/kpss/kpss-lisans-konulari-pdf/)
- [Sözel/Sayısal Muhakeme'nin Türkçe-Matematik testleri içinde yer aldığına
  dair kaynak — hangikpss.com](https://www.hangikpss.com/StaticFiles/File/150125/15-Hangi%20Ders%20Notu-TURKCE%20_compressed%20-%20hangikpss.com.pdf)
