# QuestionPool toplu soru yükleme

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

## Denetim (check) scriptleri

Aynı `serviceAccountKey.json` ile çalışırlar, hiçbir şey değiştirmezler,
sadece okuyup raporlarlar:

- `node checkPool.js` — `QuestionPool` içindeki toplam soru sayısı, kategori
  dağılımı, eksik/geçersiz alanlı veya yinelenen dokümanlar.
- `node checkAdmins.js` — `adminUsers` ve `dailyTests` koleksiyonlarının
  durumu.
- `node checkUsers.js` — Firebase Authentication'daki kullanıcılar ve
  Firestore `users` koleksiyonu.
