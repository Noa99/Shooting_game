import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Ex_09_2_2001015274 extends JFrame {
  final int windowWidth = 800;
  final int windowHeight = 500;

  public static void main(String[] args) {
    new Ex_09_2_2001015274();
  }

  public Ex_09_2_2001015274() {
    Dimension dimOfScreen = Toolkit.getDefaultToolkit().getScreenSize();

    setBounds(dimOfScreen.width / 2 - windowWidth / 2,
        dimOfScreen.height / 2 - windowHeight / 2,
        windowWidth, windowHeight);
    setResizable(false);
    setTitle("Software Development II");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    MyJPanel panel = new MyJPanel();
    Container c = getContentPane();
    c.add(panel);
    setVisible(true);
  }

  public class MyJPanel extends JPanel implements
      ActionListener, MouseListener, MouseMotionListener {
    /* 全体の設定に関する変数 */
    Dimension dimOfPanel;
    Timer timer;
    ImageIcon iconMe, iconEnemy;
    Image imgMe, imgEnemy;
    JButton startButton, restartButton, finishButton;
    boolean isStartButtonClicked = false;
    boolean isFinishButtonClicked = false;
    boolean isRestartButtonClicked = false;

    /* 自機に関する変数 */
    int myHeight, myWidth;
    int myX, myY, tempMyX;
    int gap = 100;
    int[] myMissileX = new int[3];
    int[] myMissileY = new int[3];
    boolean isMyMissileActive[] = { false, false, false };

    /* 敵機に関する変数 */
    int numOfEnemy = 12;
    int numOfAlive = numOfEnemy;
    int enemyWidth, enemyHeight;
    int[] enemyX = new int[numOfEnemy];
    int[] enemyY = new int[numOfEnemy];
    int[] enemyMove = new int[numOfEnemy];
    int[] enemyMissileX = new int[numOfEnemy];
    int[] enemyMissileY = new int[numOfEnemy];
    int[] enemyMissileSpeed = new int[numOfEnemy];
    boolean[] isEnemyAlive = new boolean[numOfEnemy];
    boolean[] isEnemyMissileActive = new boolean[numOfEnemy];
    int myHp = 3;

    /* コンストラクタ（ゲーム開始時の初期化）*/                   
    public MyJPanel() {
      // 全体の設定
      setBackground(Color.black);
      addMouseListener(this);
      addMouseMotionListener(this);
      timer = new Timer(50, this);
      timer.start();

      startButton = new JButton("START");
      startButton.addActionListener(this);

      finishButton = new JButton("FINISH");
      finishButton.addActionListener(this);
      finishButton.setVisible(false);

      restartButton = new JButton("RESTART");
      restartButton.addActionListener(this);
      restartButton.setVisible(false);

      // 画像の取り込み
      iconMe = new ImageIcon("jiki.jpg");
      imgMe = iconMe.getImage();
      myWidth = imgMe.getWidth(this);
      myHeight = imgMe.getHeight(this);

      iconEnemy = new ImageIcon("teki.jpg");
      imgEnemy = iconEnemy.getImage();
      enemyWidth = imgEnemy.getWidth(this);
      enemyHeight = imgEnemy.getHeight(this);

      // 自機と敵機の初期化
      initMyPlane();
      initEnemyPlane();
      add(startButton);
      add(finishButton);
      add(restartButton);
    }

    /* パネル上の描画 */
    public void paintComponent(Graphics g) {
      dimOfPanel = getSize();
      super.paintComponent(g);
      startButton.setBounds(dimOfPanel.width / 2 - 40, dimOfPanel.height / 2 - 15, 80, 30);
      restartButton.setBounds(dimOfPanel.width / 2 - 200, dimOfPanel.height / 2 + 100, 100, 30);
      finishButton.setBounds(dimOfPanel.width / 2 + 100, dimOfPanel.height / 2 + 100, 100, 30);

      if (isStartButtonClicked) {
        startButton.setVisible(false);

        // 3回当たったらタイマーストップ
        if (myHp <= 0) {
          timer.stop();
          Font fontMessage = new Font("Serif", Font.PLAIN, 40);
          g.setFont(fontMessage);
          g.setColor(Color.red);
          drawStringCenter(g, "Oh no!!!You LOST the game!!!", dimOfPanel.width / 2, dimOfPanel.height / 2);
          restartButton.setVisible(true);
          finishButton.setVisible(true);
          if (isFinishButtonClicked) {
            System.exit(0);
          } else if (isRestartButtonClicked) {
            restartGame(g);
          }
        }

        // 各要素の描画
        Font fontScoreHp = new Font("Serif", Font.PLAIN, 18);
        g.setFont(fontScoreHp);
        g.setColor(Color.white);
        g.drawString("Score: " + Integer.toString((numOfEnemy - numOfAlive) * 10), 20, 20);
        g.drawString("My HP: " + myHp, 120, 20);
        drawMyPlane(g); // 自機
        drawMyMissile(g); // 自機のミサイル
        drawEnemyPlane(g); // 敵機
        drawEnemyMissile(g); // 敵機のミサイル

        // 敵機を全機撃墜した時の終了処理
        if (numOfAlive == 0) {
          timer.stop();
          Font fontMessage = new Font("Serif", Font.PLAIN, 40);
          g.setFont(fontMessage);
          g.setColor(Color.yellow);
          drawStringCenter(g, "Congratulation!! You WON!!!", dimOfPanel.width / 2, dimOfPanel.height / 2);
          restartButton.setVisible(true);
          finishButton.setVisible(true);
          if (isFinishButtonClicked) {
            System.exit(0);
          } else if (isRestartButtonClicked) {
            restartGame(g);
          }
        }
      }
    }

    public static void drawStringCenter(Graphics g, String text, int x, int y) {
      FontMetrics fm = g.getFontMetrics();
      Rectangle rectText = fm.getStringBounds(text, g).getBounds();
      x = x - rectText.width / 2;
      y = y - rectText.height / 2 + fm.getMaxAscent();

      g.drawString(text, x, y);
    }

    public void restartGame(Graphics g) {
      super.paintComponent(g);
      isRestartButtonClicked = false;
      restartButton.setVisible(false);
      finishButton.setVisible(false);
      initMyPlane();
      initEnemyPlane();
      add(startButton);
      add(finishButton);
      add(restartButton);
      timer.restart();
      isStartButtonClicked = true;
      myHp = 3;
      numOfAlive = 12;
      isMyMissileActive[0] = false;
      isMyMissileActive[1] = false;
      isMyMissileActive[2] = false;
    }

    /* 一定時間ごとの処理（ActionListener に対する処理）*/        
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand() == "START") {
        isStartButtonClicked = true;
      }
      if (e.getActionCommand() == "FINISH") {
        isFinishButtonClicked = true;
      }
      if (e.getActionCommand() == "RESTART") {
        isRestartButtonClicked = true;
      }
      repaint();
    }

    /* MouseListener に対する処理 */
    // マウスボタンをクリックする
    public void mouseClicked(MouseEvent e) {
    }

    // マウスボタンを押下する
    public void mousePressed(MouseEvent e) {
      for (int i = 0; i < 3; i++) {
        if (!isMyMissileActive[i]) {
          myMissileX[i] = tempMyX + myWidth / 2;
          myMissileY[i] = myY;
          isMyMissileActive[i] = true;
          break;
        }
      }
    }

    // マウスボタンを離す
    public void mouseReleased(MouseEvent e) {
    }

    // マウスが領域外へ出る
    public void mouseExited(MouseEvent e) {
    }

    // マウスが領域内に入る
    public void mouseEntered(MouseEvent e) {
    }

    /* MouseMotionListener に対する処理 */
    // マウスを動かす
    public void mouseMoved(MouseEvent e) {
      myX = e.getX();
    }

    // マウスをドラッグする
    public void mouseDragged(MouseEvent e) {
      myX = e.getX();
    }

    /* 画像ファイルから Image クラスへの変換 */
    public Image getImg(String filename) {
      ImageIcon icon = new ImageIcon(filename);
      Image img = icon.getImage();

      return img;
    }

    /* 自機の初期化 */
    public void initMyPlane() {
      myX = windowWidth / 2;
      myY = windowHeight - 100;
      tempMyX = windowWidth / 2;
    }

    /* 敵機の初期化 */
    public void initEnemyPlane() {
      for (int i = 0; i < 7; i++) {
        enemyX[i] = 70 * i;
        enemyY[i] = 50;
      }

      for (int i = 7; i < numOfEnemy; i++) {
        enemyX[i] = 70 * (i - 6);
        enemyY[i] = 100;
      }

      for (int i = 0; i < numOfEnemy; i++) {
        isEnemyAlive[i] = true;
        enemyMove[i] = 1;
      }

      for (int i = 0; i < numOfEnemy; i++) {
        isEnemyMissileActive[i] = true;
        enemyMissileX[i] = enemyX[i] + enemyWidth / 2;
        enemyMissileY[i] = enemyY[i];
        enemyMissileSpeed[i] = 10 + (i % 6);
      }
    }

    /* 自機の描画 */
    public void drawMyPlane(Graphics g) {
      if (Math.abs(tempMyX - myX) < gap) {
        if (myX < 0) {
          myX = 0;
        } else if (myX + myWidth > dimOfPanel.width) {
          myX = dimOfPanel.width - myWidth;
        }
        tempMyX = myX;
        g.drawImage(imgMe, tempMyX, myY, this);
      } else {
        g.drawImage(imgMe, tempMyX, myY, this);
      }
    }

    /* 自機のミサイルの描画 */
    public void drawMyMissile(Graphics g) {
      for (int i = 0; i < 3; i++) {
        if (isMyMissileActive[i]) {
          // ミサイルの配置
          myMissileY[i] -= 15;
          g.setColor(Color.white);
          g.fillRect(myMissileX[i], myMissileY[i], 2, 5);
        }

        // 自機のミサイルの敵機各機への当たり判定
        for (int j = 0; j < numOfEnemy; j++) {
          if (isEnemyAlive[j] && isMyMissileActive[i]) {
            if ((myMissileX[i] >= enemyX[j]) &&
                (myMissileX[i] <= enemyX[j] + enemyWidth) &&
                (myMissileY[i] >= enemyY[j]) &&
                (myMissileY[i] <= enemyY[j] + enemyHeight)) {
              isEnemyAlive[j] = false;
              isMyMissileActive[i] = false;
              numOfAlive--;
              break;
            }
          }
        }

        // ミサイルがウィンドウ外に出たときのミサイルの再初期化
        if (myMissileY[i] < 0)
          isMyMissileActive[i] = false;
      }
    }

    /* 敵機の描画 */
    public void drawEnemyPlane(Graphics g) {
      for (int i = 0; i < numOfEnemy; i++) {
        if (isEnemyAlive[i]) {
          if (enemyX[i] > dimOfPanel.width -
              enemyWidth) {
            enemyMove[i] = -1;
          } else if (enemyX[i] < 0) {
            enemyMove[i] = 1;
          }
          enemyX[i] += enemyMove[i] * 10;
          g.drawImage(imgEnemy, enemyX[i],
              enemyY[i], this);
        }
      }
    }

    /* 敵機のミサイルの描画 */
    public void drawEnemyMissile(Graphics g) {
      for (int i = 0; i < numOfEnemy; i++) {
        // ミサイルの配置
        if (isEnemyMissileActive[i]) {
          enemyMissileY[i] += enemyMissileSpeed[i];
          g.setColor(Color.red);
          g.fillRect(enemyMissileX[i],
              enemyMissileY[i], 2, 5);
        }

        // 敵機のミサイルの自機への当たり判定
        if ((enemyMissileX[i] >= tempMyX) &&
            (enemyMissileX[i] <= tempMyX + myWidth) &&
            (enemyMissileY[i] + 5 >= myY) &&
            (enemyMissileY[i] + 5 <= myY + myHeight) && isEnemyMissileActive[i]) {
          isEnemyMissileActive[i] = false;
          myHp--;
        }

        // ミサイルがウィンドウ外に出たときのミサイルの再初期化
        if (enemyMissileY[i] > dimOfPanel.height) {
          if (isEnemyAlive[i]) {
            enemyMissileX[i] = enemyX[i] +
                enemyWidth / 2;
            enemyMissileY[i] = enemyY[i] +
                enemyHeight;
          } else {
            isEnemyMissileActive[i] = false;
          }
        }
      }
    }
  }
}
