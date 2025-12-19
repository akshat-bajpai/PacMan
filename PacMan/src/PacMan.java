import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

import javax.swing.*;

public class PacMan extends JPanel implements ActionListener,KeyListener{

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;
        
        int startX;
        int startY;

        char direction='U';
        int velocityX=0;
        int velocityY=0;

        void updateDirection(char direction){
            char prevDirection=this.direction;
            this.direction=direction;
            updateVelocity(this.direction);
            this.x+=this.velocityX;
            this.y+=this.velocityY;
            for (Block wall : walls){
                if (collision(this,wall)){
                    this.x-=this.velocityX;
                    this.y-=this.velocityY;
                    this.direction=prevDirection;
                    updateVelocity(this.direction);

                }
            }
        }

        void updateVelocity(char direction){
            if (direction=='U'){
                this.velocityX=0;
                this.velocityY=-tileSize/4;
            }else if (direction == 'D'){
                this.velocityX=0;
                this.velocityY=tileSize/4;
            }else if (direction == 'R'){
                this.velocityX=tileSize/4;
                this.velocityY=0;
            }else if (direction =='L'){
                this.velocityX=-tileSize/4;
                this.velocityY=0;
            }
        }

        Block(int x,int y,int width,int height,Image image){
            this.image=image;
            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
            this.startX=x;
            this.startY=y;

        }

        void reset(){
            this.x=this.startX;
            this.y=this.startY;
        }
    }

    private int rowCount=21;
    private int columnCount=19;
    private int tileSize=32;
    private int boardWidth=columnCount*tileSize;
    private int boardHeight=rowCount*tileSize;

    private Image wallImage;
    private Image pinkGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;
    private Image blueGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Timer gameLoop;
    char [] directions={'U','D','R','L'};
    Random random=new Random();
    int score=0;
    int lives=3;
    boolean gameOver=false;


    PacMan(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setBackground(Color.BLACK); 
        addKeyListener(this);
        setFocusable(true);


        wallImage=new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage=new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage=new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage=new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage=new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage=new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage=new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanRightImage=new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanLeftImage=new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();

        loadMap();
        for (Block ghost:ghosts){
            char newDirection=directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
            
        }
        gameLoop=new Timer(50,this);
        gameLoop.start();
    }


    public void loadMap(){
        walls=new HashSet<Block>();
        foods=new HashSet<Block>();
        ghosts=new HashSet<Block>();

        for (int r=0;r<rowCount;r++){
            for (int c=0;c<columnCount;c++){
                String row=tileMap[r];
                char tileMapChar=row.charAt(c);

                int x=c*tileSize;
                int y=r*tileSize;

                if (tileMapChar=='X'){
                    Block wall=new Block( x, y,tileSize,tileSize, wallImage);
                    walls.add(wall);
                }else if(tileMapChar=='b'){
                    Block blueGhost=new Block( x, y,tileSize,tileSize, blueGhostImage);
                    ghosts.add(blueGhost);

                }else if(tileMapChar=='r'){
                    Block redGhost=new Block( x, y,tileSize,tileSize, redGhostImage);
                    ghosts.add(redGhost);
                }else if(tileMapChar=='p'){
                    Block pinkGhost=new Block(x,y,tileSize,tileSize,pinkGhostImage);
                    ghosts.add(pinkGhost);
                }else if (tileMapChar=='o'){
                    Block orangeGhost=new Block(x,y,tileSize,tileSize,orangeGhostImage);
                    ghosts.add(orangeGhost);
                }else if (tileMapChar==' '){
                    Block food=new Block(x+14,y+14,4,4,null);
                    foods.add(food);
                }else if (tileMapChar=='P'){
                    pacman=new Block(x,y,tileSize,tileSize,pacmanRightImage);
                }
                 


            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x,pacman.y,pacman.width,pacman.height,null);

        for (Block ghost : ghosts){
            g.drawImage(ghost.image,ghost.x,ghost.y,ghost.width,ghost.height,null);
        }

        for (Block wall : walls ){
            g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
        }
        g.setColor(Color.WHITE);

        for (Block food : foods){
            g.fillRect(food.x,food.y,food.width,food.height);
        }

        g.setFont(new Font("Arial",Font.PLAIN,18));
        if (gameOver){
            g.drawString("Game Over"+String.valueOf(score),tileSize/2,tileSize/2);
        }else{
            g.drawString("x "+String.valueOf(lives)+" Score : "+String.valueOf(score),tileSize/2,tileSize/2);
        }

    }

    public void move(){
        if (pacman.x>boardWidth){
            pacman.x=-tileSize+1;
        }
        if (pacman.x<-tileSize){
            pacman.x=boardWidth;
        }
        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;

        for (Block wall : walls){
            if (collision (pacman,wall)){
                pacman.x-=pacman.velocityX;
                pacman.y-=pacman.velocityY;
                break;
            }
        }

        for (Block ghost:ghosts){
            if (collision(pacman,ghost)){
                lives-=1;
                if (lives==0){
                    gameOver=true;
                    return;
                }
                resetPosition();
            }
        }
        for (Block ghost:ghosts){
            if (ghost.x==tileSize*9 && ghost.direction!='U' && ghost.direction!='D'){
                ghost.updateDirection('U');
                ghost.x+=ghost.velocityX;
                ghost.y+=ghost.velocityY;
            }
            ghost.x+=ghost.velocityX;
            ghost.y+=ghost.velocityY; 
            for (Block wall : walls){
                if (collision(wall, ghost)||ghost.x==0 || ghost.x+ghost.width==boardWidth){
                    ghost.x-=ghost.velocityX;
                    ghost.y-=ghost.velocityY;
                    char newDirection= directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten=null;
        for (Block food : foods){
            if (collision(food,pacman)){
                foodEaten=food;
                score+=10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()){
            loadMap();
            resetPosition();
        }
    }

    public boolean collision(Block a,Block b){
        return a.x+a.width>b.x &&
                a.x<b.x+b.width &&
                a.y+a.height>b.y &&
                a.y<b.y+b.width;
    }

    public void resetPosition(){
        pacman.reset();
        pacman.velocityX=0;
        pacman.velocityY=0;

        for (Block ghost:ghosts){
            ghost.reset();
            char newDirection=directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e){
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
    public void keyPressed(KeyEvent e) {}


    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver){
            loadMap();
            resetPosition();
            lives=3;
            score=0;
            gameOver=false;
            gameLoop.start();
        }
        // System.out.println("Key Event : "+e.getKeyCode());


        if (e.getKeyCode()==KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }else if (e.getKeyCode()==KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }else if (e.getKeyCode()==KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }else if (e.getKeyCode()==KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }

        if (pacman.direction=='U'){
            pacman.image=pacmanUpImage;
        }else if (pacman.direction=='D'){
            pacman.image=pacmanDownImage;
        }else if (pacman.direction=='R'){
            pacman.image=pacmanRightImage;
        }else if (pacman.direction=='L'){
            pacman.image=pacmanLeftImage;
        }
    }
}