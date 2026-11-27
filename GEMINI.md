# KPSS Arena Proje Kuralları

## Sürüm ve Yayınlama Politikası (Versioning & Release)
- **Otomatik Sürüm Yükseltme**: Büyük güncelleme, yeni özellik, mimari optimizasyon veya Google Play yayını gerektiren değişiklikler yapıldığında `app/build.gradle` içindeki `versionName` sürüm numarasını otomatik olarak artır.
- **CI/CD Entegrasyonu**: `versionCode` GitHub Actions CI/CD üzerinde `GITHUB_RUN_NUMBER` ile artar. Yerel geliştirmede `versionName`'in güncel tutulması zorunludur.
- **Commit ve Push Temizliği**: Her görev tamamlandığında çalışma ağacında (`git status`) hiçbir dosya commit ve push edilmemiş bırakılmamalıdır.

## v2.0.0 (2026-11-27) Yapılan Büyük Geliştirmeler
- **Ana Ekran KPSS Kokpiti**: Dinamik KPSS Sınav Geri Sayımı (Lisans / Önlisans / Ortaöğretim dokunarak geçiş) + etkileşimli **KPSS Net & P3/P93/P94 Puan Hesaplayıcı** (`KpssCalculatorDialog`).
- **ÖSYM Gerçekçiliği (`MockExamFragment`)**: 5. şık (`optionE`) desteği, **🚩 Şüpheli Soru İşaretleme**, ve sayaç rozetine dokununca açılan **Etkileşimli Soru Paleti** (Dolu/Boş/İşaretli renk kodlarıyla tek tıkla soruya atlama).
- **Modern Pratik & 3D Flashcard**: Eski `Spinner` yerine yatay **Material Chip** kategori seçiciler; Flashcard'da **3D kart çevirme animasyonu** ve **Spaced Repetition (`✓ Biliyorum` / `↻ Tekrar Et`)**.
- **Kullanıcı Dostu Reklam & Değerlendirme**: Interstitial bekleme süresi `45sn → 120sn`, Google Play politikalarına tam uyumlu `ReviewPromptHelper` eklendi.
