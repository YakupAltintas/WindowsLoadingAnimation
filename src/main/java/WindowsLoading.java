import processing.core.*;

public class WindowsLoading extends PApplet {

    final int   NUM_BALLS    = 6;
    final float BALL_DELAY   = 110f;   // Bilyeler arası gecikme (ms)
    final float LAP_DURATION = 1800f;  // Bir tam tur süresi (ms)
    final float PAUSE_AFTER  = 1200f;  // Döngü sonu bekleme (ms)
    
    // Toplam döngü süresi = (Son bilyenin başlama zamanı) + (2 tur süresi) + (Bekleme)
    final float TOTAL_CYCLE = (NUM_BALLS - 1) * BALL_DELAY + 2 * LAP_DURATION + PAUSE_AFTER;

    float orbitR    = 80f;
    float ballBaseR = 4.5f;

    @Override
    public void settings() {
        size(600, 400);
        smooth(8);
    }

    @Override
    public void setup() {
        frameRate(60);
    }

    @Override
    public void draw() {
        background(0);
        float currentTime = millis() % TOTAL_CYCLE;

        translate(width / 2f, height / 2f);

        for (int i = 0; i < NUM_BALLS; i++) {
            float startTime = i * BALL_DELAY;
            float ballTime = currentTime - startTime;

            // Eğer bilye henüz başlamadıysa veya 2 turunu tamamladıysa çizme
            if (ballTime < 0 || ballTime > 2 * LAP_DURATION) continue;

            drawVideoBall(ballTime);
        }

        drawLabel();
    }

    void drawVideoBall(float ballTime) {
        // Toplam 2 tur ilerlemesi (0.0 -> 2.0)
        float totalProgress = ballTime / LAP_DURATION;
        
        // Hız Değişimi: Tepeden (3PI/2) sağa doğru hızlanma mantığı
        // Doğrusal ilerleme yerine ivmeli bir fonksiyon kullanıyoruz
        float easedProgress = getEasedProgress(totalProgress);
        
        // Açı: En alttan başla (PI/2), saat yönünde dön
        float angle = HALF_PI + easedProgress * TWO_PI;

        float x = cos(angle) * orbitR;
        float y = sin(angle) * orbitR;

        // Alpha: İkinci turun sonuna doğru yavaşça solma
        float al = 1.0f;
        if (totalProgress > 1.7f) {
            al = map(totalProgress, 1.7f, 2.0f, 1.0f, 0f);
        }
        al = constrain(al, 0, 1);

        noStroke();
        // Parlama efekti
        for (int g = 4; g >= 1; g--) {
            float gr = ballBaseR * (1f + g * 0.7f);
            fill(255, 255, 255, al * (0.07f / g) * 255f);
            ellipse(x, y, gr * 2, gr * 2);
        }

        // Bilye gövdesi
        fill(255, 255, 255, al * 255f);
        ellipse(x, y, ballBaseR * 2, ballBaseR * 2);

        // Cam efekti (highlight)
        fill(255, 255, 255, al * 180f);
        ellipse(x - ballBaseR * 0.3f, y - ballBaseR * 0.3f, ballBaseR * 0.8f, ballBaseR * 0.8f);
    }

    // İvmelenme fonksiyonu: Saat 2'de hızlan, Saat 12'de yavaşla (Her iki tur)
    float getEasedProgress(float p) {
        float vSlow = 0.6f;
        float vFast = 1.278f;

        if (p < 0.66f) {
            // 1. Tur: Saat 6 -> Saat 2 (Yavaş)
            return p * vSlow;
        } else if (p < 1.5f) {
            // 1. Tur: Saat 2 -> 2. Tur: Saat 12 (Hızlı)
            return 0.66f * vSlow + (p - 0.66f) * vFast;
        } else if (p < 1.66f) {
            // 2. Tur: Saat 12 -> Saat 2 (Yavaş)
            return 0.66f * vSlow + 0.84f * vFast + (p - 1.5f) * vSlow;
        } else {
            // 2. Tur: Saat 2 -> Saat 6 (Hızlı)
            return 0.82f * vSlow + 0.84f * vFast + (p - 1.66f) * vFast;
        }
    }

    void drawLabel() {
        resetMatrix();
        fill(255, 255, 255, 100);
        textAlign(CENTER, CENTER);
        textSize(13);
        text("Windows\u2019u Ba\u015flatma", width / 2f, height - 50f);
    }

    public static void main(String[] args) {
        PApplet.main("WindowsLoading");
    }
}
