# KPSS Arena Proje Kuralları

## Sürüm ve Yayınlama Politikası (Versioning & Release)
- **Otomatik Sürüm Yükseltme**: Büyük güncelleme, yeni özellik, mimari optimizasyon veya Google Play yayını gerektiren değişiklikler yapıldığında `app/build.gradle` içindeki `versionName` sürüm numarasını otomatik olarak artır.
- **CI/CD Entegrasyonu**: `versionCode` GitHub Actions CI/CD üzerinde `GITHUB_RUN_NUMBER` ile artar. Yerel geliştirmede `versionName`'in güncel tutulması zorunludur.
- **Commit ve Push Temizliği**: Her görev tamamlandığında çalışma ağacında (`git status`) hiçbir dosya commit ve push edilmemiş bırakılmamalıdır.
