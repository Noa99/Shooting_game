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
    /* �S�̂̐ݒ�Ɋւ���ϐ� */
    Dimension dimOfPanel;
    Timer timer;
    ImageIcon iconMe, iconEnemy;
    Image imgMe, imgEnemy;
    JButton startButton, restartButton, finishButton;
    boolean isStartButtonClicked = false;
    boolean isFinishButtonClicked = false;
    boolean isRestartButtonClicked = false;

    /* ���@�Ɋւ���ϐ� */
    int myHeight, myWidth;
    int myX, myY, tempMyX;
    int gap = 100;
    int[] myMissileX = new int[3];
    int[] myMissileY = new int[3];
    boolean isMyMissileActive[] = { false, false, false };

    /* �G�@�Ɋւ���ϐ� */
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

    /* �R���X�g���N�^�i�Q�[���J�n���̏������j*/                   
    public MyJPanel() {
      // �S�̂̐ݒ�
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

      // �摜�̎�荞��
      iconMe = new ImageIcon("jiki.jpg");
      imgMe = iconMe.getImage();
      myWidth = imgMe.getWidth(this);
      myHeight = imgMe.getHeight(this);

      iconEnemy = new ImageIcon("teki.jpg");
      imgEnemy = iconEnemy.getImage();
      enemyWidth = imgEnemy.getWidth(this);
      enemyHeight = imgEnemy.getHeight(this);

      // ���@�ƓG�@�̏�����
      initMyPlane();
      initEnemyPlane();
      add(startButton);
      add(finishButton);
      add(restartButton);
    }

    /* �p�l����̕`�� */
    public void paintComponent(Graphics g) {
      dimOfPanel = getSize();
      super.paintComponent(g);
      startButton.setBounds(dimOfPanel.width / 2 - 40, dimOfPanel.height / 2 - 15, 80, 30);
      restartButton.setBounds(dimOfPanel.width / 2 - 200, dimOfPanel.height / 2 + 100, 100, 30);
      finishButton.setBounds(dimOfPanel.width / 2 + 100, dimOfPanel.height / 2 + 100, 100, 30);

      if (isStartButtonClicked) {
        startButton.setVisible(false);

        // 3�񓖂�������^�C�}�[�X�g�b�v
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

        // �e�v�f�̕`��
        Font fontScoreHp = new Font("Serif", Font.PLAIN, 18);
        g.setFont(fontScoreHp);
        g.setColor(Color.white);
        g.drawString("Score: " + Integer.toString((numOfEnemy - numOfAlive) * 10), 20, 20);
        g.drawString("My HP: " + myHp, 120, 20);
        drawMyPlane(g); // ���@
        drawMyMissile(g); // ���@�̃~�T�C��
        drawEnemyPlane(g); // �G�@
        drawEnemyMissile(g); // �G�@�̃~�T�C��

        // �G�@��S�@���Ă������̏I������
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

    /* ��莞�Ԃ��Ƃ̏����iActionListener �ɑ΂��鏈���j*/        
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

    /* MouseListener �ɑ΂��鏈�� */
    // �}�E�X�{�^�����N���b�N����
    public void mouseClicked(MouseEvent e) {
    }

    // �}�E�X�{�^������������
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

    // �}�E�X�{�^���𗣂�
    public void mouseReleased(MouseEvent e) {
    }

    // �}�E�X���̈�O�֏o��
    public void mouseExited(MouseEvent e) {
    }

    // �}�E�X���̈���ɓ���
    public void mouseEntered(MouseEvent e) {
    }

    /* MouseMotionListener �ɑ΂��鏈�� */
    // �}�E�X�𓮂���
    public void mouseMoved(MouseEvent e) {
      myX = e.getX();
    }

    // �}�E�X���h���b�O����
    public void mouseDragged(MouseEvent e) {
      myX = e.getX();
    }

    /* �摜�t�@�C������ Image �N���X�ւ̕ϊ� */
    public Image getImg(String filename) {
      ImageIcon icon = new ImageIcon(filename);
      Image img = icon.getImage();

      return img;
    }

    /* ���@�̏����� */
    public void initMyPlane() {
      myX = windowWidth / 2;
      myY = windowHeight - 100;
      tempMyX = windowWidth / 2;
    }

    /* �G�@�̏����� */
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

    /* ���@�̕`�� */
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

    /* ���@�̃~�T�C���̕`�� */
    public void drawMyMissile(Graphics g) {
      for (int i = 0; i < 3; i++) {
        if (isMyMissileActive[i]) {
          // �~�T�C���̔z�u
          myMissileY[i] -= 15;
          g.setColor(Color.white);
          g.fillRect(myMissileX[i], myMissileY[i], 2, 5);
        }

        // ���@�̃~�T�C���̓G�@�e�@�ւ̓����蔻��
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

        // �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
        if (myMissileY[i] < 0)
          isMyMissileActive[i] = false;
      }
    }

    /* �G�@�̕`�� */
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

    /* �G�@�̃~�T�C���̕`�� */
    public void drawEnemyMissile(Graphics g) {
      for (int i = 0; i < numOfEnemy; i++) {
        // �~�T�C���̔z�u
        if (isEnemyMissileActive[i]) {
          enemyMissileY[i] += enemyMissileSpeed[i];
          g.setColor(Color.red);
          g.fillRect(enemyMissileX[i],
              enemyMissileY[i], 2, 5);
        }

        // �G�@�̃~�T�C���̎��@�ւ̓����蔻��
        if ((enemyMissileX[i] >= tempMyX) &&
            (enemyMissileX[i] <= tempMyX + myWidth) &&
            (enemyMissileY[i] + 5 >= myY) &&
            (enemyMissileY[i] + 5 <= myY + myHeight) && isEnemyMissileActive[i]) {
          isEnemyMissileActive[i] = false;
          myHp--;
        }

        // �~�T�C�����E�B���h�E�O�ɏo���Ƃ��̃~�T�C���̍ď�����
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
