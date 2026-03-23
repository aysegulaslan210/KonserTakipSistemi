package konsertakip;

import java.awt.*;
import java.awt.event.*;

public class Loginekrani extends Frame implements ActionListener {

    static final Color KOYU_MOR = new Color(90, 20, 60);
    static final Color ACIK_MOR = new Color(180, 50, 110);
    static final Color ALTIN    = new Color(255, 200, 50);
    static final Color BEYAZ    = new Color(255, 255, 255);
    static final Color GRI      = new Color(220, 215, 230);
    static final Color SIYAH    = new Color(0, 0, 0);
    static final Color KIRMIZI  = new Color(255, 80, 80);

    static final Font FONT_BASLIK = new Font("Dialog", Font.BOLD, 22);
    static final Font FONT_LABEL  = new Font("Dialog", Font.BOLD, 14);
    static final Font FONT_FIELD  = new Font("Dialog", Font.PLAIN, 13);
    static final Font FONT_BUTON  = new Font("Dialog", Font.BOLD, 14);

    // Sabit admin bilgileri
    static final String ADMIN_KULLANICI = "admin";
    static final String ADMIN_SIFRE     = "admin123";

    // Sabit kullanici bilgileri
    static final String KULLANICI_ADI   = "kullanici";
    static final String KULLANICI_SIFRE = "kullanici123";

    TextField tfKullanici, tfSifre;
    Button    btnGiris, btnCikis;
    Label     lblHata;

    public Loginekrani() {
        super("Konser Takip Sistemi - Giris");

        setSize(400, 350);
        setResizable(false);
        setBackground(KOYU_MOR);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        // Baslik paneli
        Panel pnlBaslik = new Panel(new FlowLayout(FlowLayout.CENTER));
        pnlBaslik.setBackground(ACIK_MOR);
        pnlBaslik.setPreferredSize(new Dimension(400, 60));
        Label lblBaslik = new Label("*** Konser Takip Sistemi ***", Label.CENTER);
        lblBaslik.setFont(FONT_BASLIK);
        lblBaslik.setForeground(ALTIN);
        pnlBaslik.add(lblBaslik);
        add(pnlBaslik);

        // Alt baslik
        Panel pnlAltBaslik = new Panel(new FlowLayout(FlowLayout.CENTER));
        pnlAltBaslik.setBackground(KOYU_MOR);
        pnlAltBaslik.setPreferredSize(new Dimension(400, 35));
        Label lblAlt = new Label("Sisteme Giris Yapin", Label.CENTER);
        lblAlt.setFont(new Font("Dialog", Font.ITALIC, 13));
        lblAlt.setForeground(GRI);
        pnlAltBaslik.add(lblAlt);
        add(pnlAltBaslik);

        // Form paneli
        Panel pnlForm = new Panel(new GridLayout(2, 2, 10, 10));
        pnlForm.setBackground(new Color(110, 25, 75));
        pnlForm.setPreferredSize(new Dimension(360, 90));

        Label lblKullanici = new Label("  Kullanici Adi:", Label.LEFT);
        lblKullanici.setFont(FONT_LABEL);
        lblKullanici.setForeground(GRI);

        tfKullanici = new TextField();
        tfKullanici.setFont(FONT_FIELD);
        tfKullanici.setBackground(new Color(70, 10, 50));
        tfKullanici.setForeground(BEYAZ);

        Label lblSifre = new Label("  Sifre:", Label.LEFT);
        lblSifre.setFont(FONT_LABEL);
        lblSifre.setForeground(GRI);

        tfSifre = new TextField();
        tfSifre.setFont(FONT_FIELD);
        tfSifre.setBackground(new Color(70, 10, 50));
        tfSifre.setForeground(BEYAZ);
        tfSifre.setEchoChar('*');

        pnlForm.add(lblKullanici); pnlForm.add(tfKullanici);
        pnlForm.add(lblSifre);     pnlForm.add(tfSifre);
        add(pnlForm);

        // Hata etiketi
        Panel pnlHata = new Panel(new FlowLayout(FlowLayout.CENTER));
        pnlHata.setBackground(KOYU_MOR);
        pnlHata.setPreferredSize(new Dimension(400, 30));
        lblHata = new Label("", Label.CENTER);
        lblHata.setFont(new Font("Dialog", Font.BOLD, 12));
        lblHata.setForeground(KIRMIZI);
        pnlHata.add(lblHata);
        add(pnlHata);

        // Butonlar
        Panel pnlButonlar = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButonlar.setBackground(KOYU_MOR);
        pnlButonlar.setPreferredSize(new Dimension(400, 55));

        btnGiris = new Button("GIRIS YAP");
        btnGiris.setFont(FONT_BUTON);
        btnGiris.setForeground(SIYAH);
        btnGiris.setPreferredSize(new Dimension(150, 36));
        btnGiris.addActionListener(this);

        btnCikis = new Button("CIKIS");
        btnCikis.setFont(FONT_BUTON);
        btnCikis.setForeground(SIYAH);
        btnCikis.setPreferredSize(new Dimension(100, 36));
        btnCikis.addActionListener(this);

        pnlButonlar.add(btnGiris);
        pnlButonlar.add(btnCikis);
        add(pnlButonlar);

        // Bilgi notu
        Panel pnlNot = new Panel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pnlNot.setBackground(new Color(70, 10, 50));
        pnlNot.setPreferredSize(new Dimension(400, 65));
        Label lblNot1 = new Label("Admin  : kullanici=admin     sifre=admin123", Label.CENTER);
        lblNot1.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblNot1.setForeground(new Color(180, 180, 180));
        Label lblNot2 = new Label("Kullanici: kullanici=kullanici  sifre=kullanici123", Label.CENTER);
        lblNot2.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblNot2.setForeground(new Color(180, 180, 180));
        pnlNot.add(lblNot1);
        pnlNot.add(lblNot2);
        add(pnlNot);

        // Merkeze al
        Dimension ekran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ekran.width - 400) / 2, (ekran.height - 350) / 2);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCikis) {
            dispose();
            System.exit(0);
        } else if (e.getSource() == btnGiris) {
            girisYap();
        }
    }

    private void girisYap() {
        String kullanici = tfKullanici.getText().trim();
        String sifre     = tfSifre.getText().trim();

        if (kullanici.equals(ADMIN_KULLANICI) && sifre.equals(ADMIN_SIFRE)) {
            dispose();
            new Konsertakipsistemi(true); // admin = true
        } else if (kullanici.equals(KULLANICI_ADI) && sifre.equals(KULLANICI_SIFRE)) {
            dispose();
            new Konsertakipsistemi(false); // admin = false
        } else {
            lblHata.setText("Hatali kullanici adi veya sifre!");
            tfSifre.setText("");
        }
    }

    public static void main(String[] args) {
        new Loginekrani();
    }
}