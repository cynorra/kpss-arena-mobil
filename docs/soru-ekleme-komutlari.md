# Soru Ekleme — Hızlı Referans

Her seferinde bunlara bak, detaylı açıklama `scripts/README.md`'de.

## Tek seferlik kurulum (zaten yaptıysan atla)

1. Firebase Console → **kpss-arena-2f11c** → ⚙️ Proje Ayarları → Servis
   Hesapları → **Yeni Özel Anahtar Oluştur** → indirilen dosyayı
   `scripts/serviceAccountKey.json` olarak kaydet.
2. Firestore kurallarını yayınla:
   ```
   firebase deploy --only firestore:rules
   ```
3. Bağımlılıkları kur:
   ```
   cd scripts
   npm install
   ```

## Her yeni soru grubunda yapılacaklar

1. `docs/question-generation-prompt.md`'deki prompt'u bir AI'ya yapıştır,
   `{SAYI}` yerine kaç soru istediğini yaz.
2. Çıkan JSON'u bir dosyaya kaydet, örn: `docs/sorular-2026-08.json`
   (şema: `docs/question-pattern.json`).
3. Yükle:
   ```
   cd scripts
   node uploadQuestions.js ../docs/sorular-2026-08.json
   ```
4. Konsoldaki "X soru eklendi, Y soru atlandı" satırını kontrol et — atlanan
   varsa hangi soruda hangi alan eksik/geçersiz olduğu yazar.

## Dikkat

- Aynı dosyayı iki kez yüklersen sorular **çift** eklenir (script her
  çalıştırmada yeni ID üretir). Aynı dosyayı tekrar yüklemeden önce eski
  soruları dosyadan çıkar.
- `scripts/serviceAccountKey.json` asla paylaşılmaz / commit'lenmez —
  `.gitignore`'da zaten hariç.
