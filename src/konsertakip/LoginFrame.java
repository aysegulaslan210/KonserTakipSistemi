package konsertakip;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends Frame implements ActionListener {

    // --- Renkler ---
    static final Color KOYU     = new Color(90, 20, 60);
    static final Color ORTA     = new Color(140, 40, 90);
    static final Color ACIK     = new Color(180, 50, 110);
    static final Color ALTIN    = new Color(255, 200, 50);
    static final Color BEYAZ    = new Color(255, 255, 255);
    static final Color GRI      = new Color(220, 215, 230);
    static final Color SIYAH    = new Color(0, 0, 0);
    static final Color YESIL    = new Color(80, 200, 120);
    static final Color KIRMIZI  = new Color(220, 80, 80);

    static final Font F_BASLIK = new Font("Dialog", Font.BOLD, 22);
    static final Font F_LABEL  = new Font("Dialog", Font.BOLD, 13);
    static final Font F_FIELD  = new Font("Dialog", Font.PLAIN, 13);
    static final Font F_BUTON  = new Font("Dialog", Font.BOLD, 14);
    static final Font F_KUCUK  = new Font("Dialog", Font.ITALIC, 11);

    static final String DB_URL = "jdbc:sqlite:konser.db";
    Connection connection;

    // Giris paneli bilesenleri
    Panel pnlGiris, pnlKayit;
    TextField tfGKullanici, tfGSifre;
    TextField tfKAd, tfKSoyad, tfKKullanici, tfKSifre, tfKSifre2;
    Label lblHataGiris, lblHataKayit;
    Button btnGirisYap, btnAdminGiris, btnKayitOl, btnKayitEkrani, btnGirisEkrani;

    // CardLayout icin ana panel
    Panel pnlAna;

    public LoginFrame() {
        super("Konser Takip Sistemi - Giris");
        baglantiKur();
        tabloOlustur();

        setSize(440, 500);
        setResizable(false);
        setBackground(KOYU);
        setLayout(new BorderLayout());

        // BASLIK
        Panel pnlBaslik = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 12));
        pnlBaslik.setBackground(ACIK);
        pnlBaslik.setPreferredSize(new Dimension(440, 65));
        Label lblBaslik = new Label("KONSER TAKIP SISTEMI", Label.CENTER);
        lblBaslik.setFont(F_BASLIK); lblBaslik.setForeground(ALTIN);
        Label lblAlt = new Label("Giris yapın veya kayit olun", Label.CENTER);
        lblAlt.setFont(F_KUCUK); lblAlt.setForeground(GRI);
        pnlBaslik.add(lblBaslik); pnlBaslik.add(lblAlt);
        add(pnlBaslik, BorderLayout.NORTH);

        // KART PANELI
        pnlAna = new Panel(new CardLayout());
        pnlAna.setBackground(KOYU);
        olusturGirisEkrani();
        olusturKayitEkrani();
        pnlAna.add(pnlGiris, "giris");
        pnlAna.add(pnlKayit, "kayit");
        add(pnlAna, BorderLayout.CENTER);

        Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ekran.width - 440) / 2, (ekran.height - 500) / 2);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { baglantiKapat(); dispose(); System.exit(0); }
        });
    }

    private void olusturGirisEkrani() {
        pnlGiris = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        pnlGiris.setBackground(KOYU);

        Panel pForm = new Panel(new GridLayout(2, 2, 10, 12));
        pForm.setBackground(ORTA);
        pForm.setPreferredSize(new Dimension(380, 90));

        Label lKul = new Label("  Kullanici Adi:"); stilLabel(lKul);
        tfGKullanici = new TextField(); stilField(tfGKullanici);
        Label lSif = new Label("  Sifre:"); stilLabel(lSif);
        tfGSifre = new TextField(); tfGSifre.setEchoChar('*'); stilField(tfGSifre);
        pForm.add(lKul); pForm.add(tfGKullanici);
        pForm.add(lSif); pForm.add(tfGSifre);
        pnlGiris.add(pForm);

        lblHataGiris = new Label("", Label.CENTER);
        lblHataGiris.setFont(new Font("Dialog", Font.BOLD, 12));
        lblHataGiris.setForeground(KIRMIZI);
        lblHataGiris.setPreferredSize(new Dimension(380, 20));
        pnlGiris.add(lblHataGiris);

        Panel pBtn = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pBtn.setBackground(KOYU);
        pBtn.setPreferredSize(new Dimension(380, 50));

        btnGirisYap = new Button("KULLANICI GIRISI");
        btnGirisYap.setFont(F_BUTON); btnGirisYap.setForeground(SIYAH);
        btnGirisYap.setPreferredSize(new Dimension(170, 36));
        btnGirisYap.addActionListener(this);

        btnAdminGiris = new Button("ADMIN GIRISI");
        btnAdminGiris.setFont(F_BUTON); btnAdminGiris.setForeground(SIYAH);
        btnAdminGiris.setPreferredSize(new Dimension(150, 36));
        btnAdminGiris.addActionListener(this);

        pBtn.add(btnGirisYap); pBtn.add(btnAdminGiris);
        pnlGiris.add(pBtn);

        Panel pAlt = new Panel(new FlowLayout(FlowLayout.CENTER));
        pAlt.setBackground(KOYU);
        pAlt.setPreferredSize(new Dimension(380, 40));
        Label lblSoru = new Label("Hesabiniz yok mu?");
        lblSoru.setFont(F_KUCUK); lblSoru.setForeground(GRI);
        btnKayitEkrani = new Button("KAYIT OL");
        btnKayitEkrani.setFont(new Font("Dialog", Font.BOLD, 12));
        btnKayitEkrani.setForeground(SIYAH);
        btnKayitEkrani.setPreferredSize(new Dimension(100, 26));
        btnKayitEkrani.addActionListener(this);
        pAlt.add(lblSoru); pAlt.add(btnKayitEkrani);
        pnlGiris.add(pAlt);

        Label lblAdminNot = new Label("Admin girisi: admin / admin123", Label.CENTER);
        lblAdminNot.setFont(F_KUCUK); lblAdminNot.setForeground(new Color(180, 180, 180));
        pnlGiris.add(lblAdminNot);
    }

    private void olusturKayitEkrani() {
        pnlKayit = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlKayit.setBackground(KOYU);

        Panel pBaslik = new Panel(new FlowLayout(FlowLayout.CENTER));
        pBaslik.setBackground(ORTA);
        pBaslik.setPreferredSize(new Dimension(380, 35));
        Label lbl = new Label("Yeni Hesap Olustur", Label.CENTER);
        lbl.setFont(new Font("Dialog", Font.BOLD, 15)); lbl.setForeground(ALTIN);
        pBaslik.add(lbl); pnlKayit.add(pBaslik);

        Panel pForm = new Panel(new GridLayout(5, 2, 10, 10));
        pForm.setBackground(ORTA);
        pForm.setPreferredSize(new Dimension(380, 200));

        Label lAd = new Label("  Ad:"); stilLabel(lAd);
        tfKAd = new TextField(); stilField(tfKAd);
        pForm.add(lAd); pForm.add(tfKAd);

        Label lSoy = new Label("  Soyad:"); stilLabel(lSoy);
        tfKSoyad = new TextField(); stilField(tfKSoyad);
        pForm.add(lSoy); pForm.add(tfKSoyad);

        Label lKul = new Label("  Kullanici Adi:"); stilLabel(lKul);
        tfKKullanici = new TextField(); stilField(tfKKullanici);
        pForm.add(lKul); pForm.add(tfKKullanici);

        Label lSif = new Label("  Sifre:"); stilLabel(lSif);
        tfKSifre = new TextField(); tfKSifre.setEchoChar('*'); stilField(tfKSifre);
        pForm.add(lSif); pForm.add(tfKSifre);

        Label lSif2 = new Label("  Sifre Tekrar:"); stilLabel(lSif2);
        tfKSifre2 = new TextField(); tfKSifre2.setEchoChar('*'); stilField(tfKSifre2);
        pForm.add(lSif2); pForm.add(tfKSifre2);

        pnlKayit.add(pForm);

        lblHataKayit = new Label("", Label.CENTER);
        lblHataKayit.setFont(new Font("Dialog", Font.BOLD, 12));
        lblHataKayit.setForeground(KIRMIZI);
        lblHataKayit.setPreferredSize(new Dimension(380, 20));
        pnlKayit.add(lblHataKayit);

        Panel pBtn = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pBtn.setBackground(KOYU);
        pBtn.setPreferredSize(new Dimension(380, 45));

        btnKayitOl = new Button("KAYIT OL");
        btnKayitOl.setFont(F_BUTON); btnKayitOl.setForeground(SIYAH);
        btnKayitOl.setPreferredSize(new Dimension(140, 34));
        btnKayitOl.addActionListener(this);

        btnGirisEkrani = new Button("<< GIRISE DON");
        btnGirisEkrani.setFont(new Font("Dialog", Font.BOLD, 12)); btnGirisEkrani.setForeground(SIYAH);
        btnGirisEkrani.setPreferredSize(new Dimension(140, 34));
        btnGirisEkrani.addActionListener(this);

        pBtn.add(btnKayitOl); pBtn.add(btnGirisEkrani);
        pnlKayit.add(pBtn);
    }

    public void actionPerformed(ActionEvent e) {
        Object k = e.getSource();
        CardLayout cl = (CardLayout) pnlAna.getLayout();

        if (k == btnKayitEkrani) { cl.show(pnlAna, "kayit"); return; }
        if (k == btnGirisEkrani) { cl.show(pnlAna, "giris"); return; }

        if (k == btnAdminGiris) {
            String kul = tfGKullanici.getText().trim();
            String sif = tfGSifre.getText().trim();
            if (kul.equals("admin") && sif.equals("admin123")) {
                dispose();
                new Adminframe(connection);
            } else {
                lblHataGiris.setText("Hatali admin bilgileri!");
                lblHataGiris.setForeground(KIRMIZI);
            }
        }

        if (k == btnGirisYap) {
            String kul = tfGKullanici.getText().trim();
            String sif = tfGSifre.getText().trim();
            if (kul.isEmpty() || sif.isEmpty()) {
                lblHataGiris.setText("Kullanici adi ve sifre bos olamaz!");
                return;
            }
            int[] sonuc = kullaniciGirisKontrol(kul, sif);
            if (sonuc[0] != -1) {
                dispose();
                new Kullaniciframe(connection, sonuc[0], kul);
            } else {
                lblHataGiris.setText("Hatali kullanici adi veya sifre!");
                lblHataGiris.setForeground(KIRMIZI);
            }
        }

        if (k == btnKayitOl) {
            String ad   = tfKAd.getText().trim();
            String soy  = tfKSoyad.getText().trim();
            String kul  = tfKKullanici.getText().trim();
            String sif  = tfKSifre.getText().trim();
            String sif2 = tfKSifre2.getText().trim();

            if (ad.isEmpty() || soy.isEmpty() || kul.isEmpty() || sif.isEmpty()) {
                lblHataKayit.setText("Tum alanlar zorunludur!"); return;
            }
            if (!sif.equals(sif2)) {
                lblHataKayit.setText("Sifreler uyusmuyor!"); return;
            }
            if (kullaniciVarMi(kul)) {
                lblHataKayit.setText("Bu kullanici adi zaten alinmis!"); return;
            }
            kayitOl(ad, soy, kul, sif);
            lblHataKayit.setText("Kayit basarili! Giris yapabilirsiniz.");
            lblHataKayit.setForeground(YESIL);
        }
    }

    private int[] kullaniciGirisKontrol(String kul, String sif) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id FROM Kullanici WHERE kullanici_adi=? AND sifre=?");
            ps.setString(1, kul); ps.setString(2, sif);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new int[]{rs.getInt("id")};
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
        return new int[]{-1};
    }

    private boolean kullaniciVarMi(String kul) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id FROM Kullanici WHERE kullanici_adi=?");
            ps.setString(1, kul);
            return ps.executeQuery().next();
        } catch (SQLException ex) { return false; }
    }

    private void kayitOl(String ad, String soy, String kul, String sif) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Kullanici (ad, soyad, kullanici_adi, sifre) VALUES (?,?,?,?)");
            ps.setString(1, ad); ps.setString(2, soy);
            ps.setString(3, kul); ps.setString(4, sif);
            ps.executeUpdate();
        } catch (SQLException ex) { System.out.println(ex.getMessage()); }
    }

    private void baglantiKur() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (Exception ex) { System.out.println("Baglanti hatasi: " + ex.getMessage()); }
    }

    private void tabloOlustur() {
        try {
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS Kullanici (" +
                    "id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ad             TEXT NOT NULL," +
                    "soyad          TEXT NOT NULL," +
                    "kullanici_adi  TEXT NOT NULL UNIQUE," +
                    "sifre          TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS Konser (" +
                    "id       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ad       TEXT NOT NULL," +
                    "tarih    TEXT NOT NULL," +
                    "konum    TEXT NOT NULL," +
                    "fiyat    REAL NOT NULL," +
                    "tur      TEXT NOT NULL DEFAULT 'Pop'," +
                    "kapasite INTEGER NOT NULL DEFAULT 100," +
                    "satilan  INTEGER NOT NULL DEFAULT 0)");
            try { st.execute("ALTER TABLE Konser ADD COLUMN kapasite INTEGER NOT NULL DEFAULT 100"); } catch(SQLException ignore){}
            try { st.execute("ALTER TABLE Konser ADD COLUMN satilan  INTEGER NOT NULL DEFAULT 0");   } catch(SQLException ignore){}
            st.execute("CREATE TABLE IF NOT EXISTS Bilet (" +
                    "id                INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "kullanici_id      INTEGER NOT NULL," +
                    "konser_id         INTEGER NOT NULL," +
                    "konser_ad         TEXT NOT NULL," +
                    "konser_tarih      TEXT NOT NULL," +
                    "konser_konum      TEXT NOT NULL," +
                    "konser_tur        TEXT NOT NULL DEFAULT 'Pop'," +
                    "adet              INTEGER NOT NULL," +
                    "birim_fiyat       REAL NOT NULL," +
                    "toplam            REAL NOT NULL," +
                    "satin_alma_tarihi TEXT NOT NULL)");
            try { st.execute("ALTER TABLE Bilet ADD COLUMN kullanici_id INTEGER NOT NULL DEFAULT 0"); } catch(SQLException ignore){}
            try { st.execute("ALTER TABLE Bilet ADD COLUMN konser_tur   TEXT NOT NULL DEFAULT 'Pop'"); } catch(SQLException ignore){}
            st.execute("CREATE TABLE IF NOT EXISTS Favori (" +
                    "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "kullanici_id INTEGER NOT NULL," +
                    "konser_id    INTEGER NOT NULL," +
                    "UNIQUE(kullanici_id, konser_id))");
            st.execute("CREATE TABLE IF NOT EXISTS Yorum (" +
                    "id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "kullanici_id INTEGER NOT NULL," +
                    "konser_id    INTEGER NOT NULL," +
                    "puan         INTEGER NOT NULL," +
                    "yorum_metni  TEXT NOT NULL," +
                    "tarih        TEXT NOT NULL)");
        } catch (SQLException ex) { System.out.println("Tablo hatasi: " + ex.getMessage()); }
    }

    private void baglantiKapat() {
        try { if (connection != null) connection.close(); } catch (SQLException ex) {}
    }

    private void stilLabel(Label l) { l.setFont(F_LABEL); l.setForeground(GRI); }
    private void stilField(TextField tf) {
        tf.setFont(F_FIELD);
        tf.setBackground(new Color(70, 10, 50));
        tf.setForeground(BEYAZ);
    }
}
