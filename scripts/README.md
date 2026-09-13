# QuestionPool soru ekleme (otomatik + manuel)

`QuestionPool` (Firestore) koleksiyonuna, `docs/question-pattern.json` şemasındaki
gibi hazırlanmış bir JSON dosyasını toplu olarak yüklemek için.

## 1) Servis hesabı anahtarını indir

Firebase Console → **kpss-arena-2f11c** projesi → ⚙️ Proje Ayarları →
**Servis Hesapları** sekmesi → **Yeni Özel Anahtar Oluştur**. İnen `.json`
dosyasını bu klasöre `serviceAccountKey.json` adıyla koy.

Bu dosyayı **asla** git'e commit'leme — `.gitignore`'da zaten hariç tutuldu.
Bu dosyaya sahip olan herkes projenin backend'ine tam admin erişimi kazanır.

## 2) Bağımlılıkları kur

```
cd scripts
npm install
```

## 3) Soruları yükle

Sorularını `docs/question-generation-prompt.md`'deki şemaya göre bir `.json`
dosyasına hazırla (örnek: `docs/question-pattern.json`), sonra:

```
node uploadQuestions.js ../docs/question-pattern.json
```

veya kendi dosyanın yolunu ver:

```
node uploadQuestions.js "C:\yol\benim-sorularim.json"
```

Script her soruyu doğrular (zorunlu alanlar, geçerli `correct`/`category`
değerleri), geçersiz olanları atlayıp konsola yazar, geçerli olanları
`QuestionPool`'a ekler. Aynı dosyayı tekrar çalıştırmak sorun değil — `id`
alanı verilmemişse her çalıştırmada yeni rastgele ID üretilir, yani aynı
dosyayı iki kez yüklersen sorular **çift** eklenir; tekrar yüklemek
istemiyorsan dosyadan önceki soruları çıkar.

Bu script Admin SDK ile çalıştığı için Firestore güvenlik kurallarından
etkilenmez (`firestore.rules` deploy edilmiş olmasa da script çalışır) —
ama uygulamanın Arena/Test ekranlarının bu soruları **okuyabilmesi** için
kuralların deploy edilmiş olması hâlâ gerekli:

```
firebase deploy --only firestore:rules
```

## Otomatik üretim (Gemini + haftalık GitHub Actions)

`generateQuestions.js`, `docs/question-generation-prompt.md`'deki aynı şema
ve müfredatı kullanarak Google Gemini'den yapılandırılmış (structured output)
JSON ister, doğrular (zorunlu alanlar + şık tekrarı/boşluk kontrolü), **havuzdaki
mevcut sorularla metin bazlı tekrar (duplicate) kontrolü** yapar ve geçen
soruları doğrudan `QuestionPool`'a yazar — onay kuyruğu yok, elle kopyala-
yapıştıra gerek yok.

```
GEMINI_API_KEY=... node generateQuestions.js 40
```

Kategori dağılımı her çalıştırmada gerçek ÖSYM oranlarına göre (Türkçe %25,
Matematik %25, Tarih %22.5, Coğrafya %15, Vatandaşlık %7.5, Güncel Bilgiler
%5) otomatik hesaplanır. Her soru `id` olarak metninin normalize edilmiş
hash'ini kullanır, bu yüzden aynı script'i tekrar çalıştırmak (aynı üretimi)
çift kayıt oluşturmaz — sadece havuzda gerçekten olmayan yeni sorular eklenir.
Eklenen sorular `source: "ai-auto"` alanıyla işaretlenir, manuel eklenenlerden
ayırt edilebilir.

**Bu, `.github/workflows/generate-questions.yml` ile her Pazartesi otomatik
çalışır**, ayrıca GitHub → Actions → "Weekly Question Generation" →
**Run workflow** ile istediğin an elle de (istediğin adet ile) tetiklenebilir.

Çalışması için repo **Settings → Secrets and variables → Actions** altında
iki secret gerekir:

- `GEMINI_API_KEY` — Google AI Studio'dan alınan Gemini API anahtarı.
- `FIRESTORE_SERVICE_ACCOUNT_B64` — bu klasördeki `serviceAccountKey.json`
  dosyasının base64'e çevrilmiş hâli:
  ```
  # PowerShell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("scripts/serviceAccountKey.json")) | Set-Clipboard
  # veya bash
  base64 -w0 scripts/serviceAccountKey.json | xclip -selection clipboard
  ```
  (`secrets.FIREBASE_SERVICE_ACCOUNT` zaten var ama o App Distribution'a
  yetkili — Firestore yazma izni olmayabilir, o yüzden ayrı bir secret.)

## Denetim (check) scriptleri

Aynı `serviceAccountKey.json` ile çalışırlar, hiçbir şey değiştirmezler,
sadece okuyup raporlarlar:

- `node checkPool.js` — `QuestionPool` içindeki toplam soru sayısı, kategori
  dağılımı, eksik/geçersiz alanlı veya yinelenen dokümanlar.
- `node checkAdmins.js` — `adminUsers` ve `dailyTests` koleksiyonlarının
  durumu.
- `node checkUsers.js` — Firebase Authentication'daki kullanıcılar ve
  Firestore `users` koleksiyonu.
