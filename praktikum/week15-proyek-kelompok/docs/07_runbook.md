# Agri-POS – Runbook

## Prasyarat
- Java 21, Maven 3.9+, PostgreSQL 12+.

## Setup DB
- Jalankan DDL/seed di `sql/schema.sql` dan `seed.sql` jika tersedia.
- Pastikan kredensial DB sesuai di kode.

## Build & Run
```bash
mvn clean compile
```

Output: Compiled classes akan tersimpan di `target/classes/`

### 3.3 Build Lengkap
```bash
mvn clean package
```

Output: JAR file akan tersimpan di `target/agripos-1.0.jar` (atau sesuai artifactId)

---

## 4. Menjalankan Aplikasi

### 4.1 Run dengan Maven
```bash
mvn javafx:run
```

Alternatif dengan plugin exec:
```bash
mvn exec:java -Dexec.mainClass="com.upb.agripos.AppJavaFX"
```

### 4.2 Run dari JAR
```bash
java -jar target/agripos-1.0.jar
```

### 4.3 Run dengan Debug Mode
```bash
mvn javafx:run -X
```

### 4.4 Expected Output
Saat aplikasi startup:
```
[INFO] --- javafx-maven-plugin:0.0.8:run (default-cli) @ agripos ---
[INFO] No module (automatic module support is available via --add-modules)
Launching Agri-POS Application...
[Login Window akan terbuka]
```

---

## 5. Verifikasi Aplikasi

### 5.1 Login Test
1. Aplikasi menampilkan login screen
2. Masukkan kredensial:
   - Username: `admin001`
   - Password: `admin123`
3. Klik Login
4. Jika berhasil, akan menampilkan dashboard admin

### 5.2 Database Connection Test
Pada startup, aplikasi akan test koneksi database:
- Jika sukses: Tidak ada error pada console
- Jika gagal: Error message akan tampil di console

### 5.3 Functional Test
Jalankan unit test:
```bash
mvn test
```

Output:
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.upb.agripos.CartServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0
```

---

## 6. Troubleshooting & Solusi

### 6.1 Java Not Found / JAVA_HOME Not Set
**Error**: `'java' is not recognized as an internal or external command`

**Solusi**:
1. Download & install JDK 21
2. Set JAVA_HOME environment variable:
   - Windows: Control Panel → Environment Variables
   - Add: `JAVA_HOME = C:\Program Files\Java\jdk-21`
3. Restart terminal
4. Verify: `java -version`

### 6.2 Maven Not Found
**Error**: `'mvn' is not recognized as an internal or external command`

**Solusi**:
1. Download Apache Maven
2. Extract ke folder, misal: `C:\apache-maven-3.9.0`
3. Set environment variable:
   - `MAVEN_HOME = C:\apache-maven-3.9.0`
   - Add ke PATH: `%MAVEN_HOME%\bin`
4. Restart terminal
5. Verify: `mvn -version`

### 6.3 Database Connection Failed
**Error**: `org.postgresql.util.PSQLException: Connection to localhost:5432 refused`

**Solusi**:
1. Cek PostgreSQL server running:
   - Windows: Services → PostgreSQL Database Server (start)
   - Linux: `sudo systemctl start postgresql`
2. Cek kredensial di kode (username, password, host, port)
3. Cek database & user sudah dibuat:
   ```bash
   psql -U postgres
   \l  -- list databases
   \du -- list users
   ```
4. Re-run database setup script jika perlu

### 6.4 Login Gagal / User Tidak Found
**Error**: `User not found` atau authentication error

**Solusi**:
1. Pastikan `sql/schema.sql` sudah dijalankan untuk create tables
2. Pastikan `sql/seed.sql` sudah dijalankan untuk insert default users
3. Verify data di database:
   ```bash
   psql -U agripos_user -d agripos_db
   SELECT * FROM users;
   ```
4. Manual insert jika belum ada:
   ```sql
   INSERT INTO users (username, password, role) 
   VALUES ('admin1', 'admin123', 'ADMIN');
   ```

### 6.5 Tabel Tidak Ditemukan
**Error**: `ERROR: relation "products" does not exist`

**Solusi**:
1. Cek koneksi database correct
2. Run schema.sql lagi:
   ```bash
   psql -U agripos_user -d agripos_db -f sql/schema.sql
   ```
3. List tables di database:
   ```bash
   psql -U agripos_user -d agripos_db
   \dt  -- list tables
   ```

### 6.6 Laporan Kosong / Data Tidak Muncul
**Error**: Laporan harian menampilkan 0 transaksi meski ada penjualan

**Solusi**:
1. Verifikasi transaksi tersimpan:
   ```bash
   psql -U agripos_user -d agripos_db
   SELECT * FROM transactions;
   ```
2. Cek tanggal transaksi sesuai dengan filter laporan
3. Debug query di aplikasi:
   - Tambah log di `ReportService.java`
   - Cek parameter tanggal dikirim dengan benar

### 6.7 Struk Tidak Muncul
**Error**: Setelah checkout, popup struk tidak tampil

**Solusi**:
1. Cek JavaFX popup rendering:
   - Buka `PosController.java` → `showReceipt()` method
   - Pastikan Stage.show() dipanggil
2. Cek event handler checkout:
   - Pastikan `checkout()` method tidak crash
   - Lihat console untuk error messages
3. Cek window manager tidak memblokir popup
4. Restart aplikasi

### 6.8 Stok Berkurang Tapi Transaksi Tidak Muncul
**Error**: Stok update tapi transaksi tidak tersimpan

**Solusi**:
1. Cek error handling di `PaymentService.java`
2. Verifikasi transaction rollback berjalan:
   ```bash
   psql -U agripos_user -d agripos_db
   BEGIN;
   SELECT * FROM products WHERE id = X;
   ROLLBACK;
   ```
3. Tambah logging di DAO class untuk trace issue

### 6.9 Performance Issues / App Lambat
**Masalah**: Aplikasi loading lambat atau freeze

**Solusi**:
1. Cek memory allocation:
   ```bash
   mvn javafx:run -Xmx512m
   ```
2. Optimize database queries:
   - Tambah indexes di frequently queried columns
   - Contoh: `CREATE INDEX idx_trans_date ON transactions(transaction_date);`
3. Cek network latency ke database:
   ```bash
   ping localhost
   ```

### 6.10 Compile Error / JAR Not Found
**Error**: `class not found` atau Maven compile fails

**Solusi**:
1. Clean & rebuild:
   ```bash
   mvn clean compile package
   ```
2. Check pom.xml dependencies valid
3. Verify Java source compatibility:
   ```xml
   <properties>
       <maven.compiler.source>21</maven.compiler.source>
       <maven.compiler.target>21</maven.compiler.target>
   </properties>
   ```

---

## 7. Development & Debugging

### 7.1 Enable Debug Logging
Edit `src/main/resources/logback.xml` (atau create):
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="DEBUG">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 7.2 Run Tests
```bash
mvn test
mvn test -Dtest=CartServiceTest
mvn test -DargLine="-Xmx1024m"
```

### 7.3 Generate Code Coverage
```bash
mvn jacoco:jacoco
mvn jacoco:report
```

Report akan di-generate di: `target/site/jacoco/index.html`

### 7.4 IDE Setup (IntelliJ IDEA / VS Code)
**IntelliJ IDEA**:
1. File → Open → Select project folder
2. IntelliJ auto-detect Maven project
3. Maven panel akan appear di right side
4. Run → Edit Configurations → Add "Maven"
5. Command: `javafx:run`

**VS Code**:
1. Install Extension Palette
2. Run: `code .`
3. Install Java Extension Pack
4. Open terminal & run: `mvn javafx:run`

---

## 8. Deployment Checklist

Sebelum go-live ke production:

- [ ] Database backup procedure sudah ada
- [ ] Kredensial database changed dari default
- [ ] Error logging configured
- [ ] User training completed
- [ ] Documentation updated
- [ ] Rollback plan available
- [ ] Database capacity checked (disk space, connections)
- [ ] Security patches applied (Java, PostgreSQL, Maven)
- [ ] Performance testing passed
- [ ] Data migration plan ready (jika ada legacy data)

---

## 9. Support & Reference

- **Documentation**: Lihat folder `docs/`
- **Database Schema**: `sql/schema.sql`
- **Project Info**: `pom.xml`
- **Source Code**: `src/main/java/com/upb/agripos/`
- **Tests**: `src/test/java/`


