/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiroparabolico;

import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * @author mrquorr
 * @author bernardot
 * @version 1.5 28/02/2014
 */
public class JFrameTiroParabolico extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private Caparazon caparazon; //objeto del caparazon
    private Tortuga tortuga; //objeto de la tortuga
    private int score; // la puntacion del juego
    private int vidas;  // vidas
    private int contadorVidas;  // numero de veces antes de perder vida
    private float factorGravedad; // aumentara la gravedad
    private float factorAumento;    // ayudara a aumentar la velocidad en x
    private float factorAumentoY;   // ayudara a aumentar la velocidad en y
    private Image dbImage;    // Imagen a proyectar
    private Image background;   // imagen que sera background
    private Graphics dbg;   // Objeto grafico
    private String instr1; // String que contiene las instrucciones del juego.
    private String instr2; // String que contiene las instrucciones del juego.
    private String instr3;  // String que contiene las instrucciones del juego.
    private String instr4;  // String que contiene las instrucciones del juego
    private SoundClip shell;    // Objeto Soundclip triste
    private SoundClip catched;  // Objeto SoundClip alegre
    private String nombreArchivo;   // Nombre del archivo
    private String[] arr;   // Arreglo del archivo dividido

    // banderas
    // de movimiento del caparazon
    private boolean click;
    private boolean volando;
    //de movimiento de la tortuga
    private boolean derecha;
    //generales
    private boolean izquierda;
    private boolean gameOver;   // saber si se acabaron las vidas
    private boolean pausado;    // Valor booleano para saber si el JFrame esta en pausa
    private boolean instrucciones;  // Valor booleano para mostrar/quitar instrucciones
    private boolean cargar; // Valor booleano para cargar el juego
    private boolean guardar;    // Valor booleano para guardar el juego
    private boolean sonido; // Valor booleano para controlar el sonido
    private boolean colision;

    // Imagenes adicionales para ambientizar el juego
    private Image im1;
    private Image im2;
    private Image block;
    private Image plant;

    // Variables auxilares para leer archivo y actualizar
    private int cPosX;  // caparazon PosX
    private int cPosY;  // caparazon PosY
    private float cVelX;    // caparazon VelX
    private float cVelY;    // caparazon VelY
    private boolean c;  // click
    private boolean v;  // volando
    private int tPosX;  // tortuga PosX
    private int tPosY; // tortuga PosY
    private float tVelX;    // tortugaVelX
    private int scr;    // score
    private int vid; // vidas
    private int contVid;    // contadordeVidas
    private float fG;   // factorGravedad
    private float fA;   // factorAumento
    private float fAY;  // factorAumentoY
    private boolean son;    // Sonido

    /**
     *  Metodo constructor del JFrame donde se inicializan las variables y se empieza el thread de
     *  <code>JFrame</code>.
     */
    public JFrameTiroParabolico() {
        //Se inicializan variables
        sonido = true;
        pausado = false;
        instrucciones = true;
        // Se cargan las instrucciones
        instr1 = "El juego consiste en intentar atrapar el caparazón con la tortuga. Al momento de darle clic al caparazón, este se "; // Instrucciones del juego
        instr2 = "moverá a través de la pantalla. Con las teclas izquierda y derecha, podrás mover a la tortuga. Si no llegas ";
        instr3 = "atrapar el caparazón, el caparazón caerá más rápido... ¡CUIDADO! ¡TU PUEDES!";
        instr4 = "Teclas: P = pausa. I = instrucciones. S = sonido. C = Cargar. G = guardar";

        // Se cargan los sonidos
        shell = new SoundClip("sounds/stomp.wav");
        catched = new SoundClip("sounds/marioSound.wav");

        nombreArchivo = "Datos.txt";
        cargar = false;
        guardar = false;

        // Se inicializan valores del juego
        score = 0;
        vidas = 5;
        contadorVidas = 3;
        gameOver = false;
        factorGravedad = (float) .5;
        factorAumento = 1;
        factorAumentoY = 1;
        // Ningun objeto se mueve
        derecha = false;
        izquierda = false;
        click = false;
        volando = false;

        // Se inicializan las imagenes ambientales
        URL bURL = this.getClass().getResource("images/back.png");
        background = Toolkit.getDefaultToolkit().getImage(bURL);
        URL cURL = this.getClass().getResource("images/redShell4.png");
        URL dURL = this.getClass().getResource("images/koopa2_2.png");
        URL eURL = this.getClass().getResource("images/block.png");
        URL fURL = this.getClass().getResource("images/plant.png");
        im1 = Toolkit.getDefaultToolkit().getImage(cURL);
        im2 = Toolkit.getDefaultToolkit().getImage(dURL);
        block = Toolkit.getDefaultToolkit().getImage(eURL);
        plant = Toolkit.getDefaultToolkit().getImage(fURL);

        //Tamaño del JFrame
        setSize(800, 500);
        //Se crean objetos
        tortuga = new Tortuga(getWidth() / 2, (getHeight() - 120));
        caparazon = new Caparazon(5, getHeight() / 2);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    /**
     *  Metodo onde empieza el hilo del 
     *  <code>JFrame</code>.
     */
    public void run() {

        while (true) {
            if (!pausado) {
                actualiza();
                checaColision();
            }

            // Se actualiza el <code>JFrame</code> repintando el contenido.
            repaint();

            try {
                // El thread se duerme.
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                System.out.println("Error en " + ex.toString());
            }
        }
    }

    /**
     * Metodo usado para actualizar la posicion de objetos tortuga y caparazon.
     *
     */
    public void actualiza() {
        //Si el juego no ha terminado hacer
        if (!gameOver) {
            // Si la bandera de pausa esta apagada
            if (!pausado) {
                // Si el caparazon se le acaba de dar click y va volando
                if (click && volando) {
                    int opcion = (int) ((Math.random() * 5)) + 6; //da la opcion sobre la distancia final en x
                    caparazon.setVelX(opcion * factorAumento);
                    caparazon.setVelY(-16 * factorAumentoY);
                    click = false;
                }
                // Si el contador de oportunidades antes de perder vida es 0
                if (contadorVidas < 1) {
                    vidas--;
                    factorGravedad += 0.5;
                    factorAumento += 0.3;
                    factorAumentoY += 0.3;
                    contadorVidas = 3;
                }
                // Si se acaban las vidas
                if (vidas == 0) {
                    gameOver = true;
                }
                // Mientras el objeto va volando
                if (volando) {
                    caparazon.gravedad(factorGravedad);
                    int x = (int) Math.round(caparazon.getVelX());
                    int y = (int) Math.round(caparazon.getVelY());
                    caparazon.setPosX(caparazon.getPosX() + x);
                    caparazon.setPosY(caparazon.getPosY() + y);
                }
                // Si se esta presionando la tecla izquierda
                if (izquierda) {
                    tortuga.setVelX(tortuga.getVelX() - 2);
                }
                // Si se esta presionando la tecla derecha
                if (derecha) {
                    tortuga.setVelX(tortuga.getVelX() + 2);
                }
                // Actualizar la posicion de la tortuga
                int x = (int) Math.round(tortuga.getVelX());
                tortuga.setPosX(x + tortuga.getPosX());
                tortuga.gravedad();
                // Si se prendio la bandera de colision, reacomodar el caparazon
                if (colision) {
                    if (sonido) {
                        catched.play();
                    }
                    caparazon.setPosX(0);
                    caparazon.setPosY(getHeight() / 2);
                    caparazon.setVelX(0);
                    caparazon.setVelY(0);
                    volando = false;
                    colision = false;
                    // Aumentar en dos el score
                    score += 2;
                }
                // Si se quiere cargar el juego (Asegurarse de que no este en instrucciones)
                if (cargar && !instrucciones) {
                    try {
                        leeArchivo();
                        // asignar valores usando los auxiliares
                        caparazon.setPosX(cPosX);
                        caparazon.setPosY(cPosY);
                        caparazon.setVelX(cVelX);
                        caparazon.setVelY(cVelY);
                        click = c;
                        volando = v;
                        tortuga.setPosX(tPosX);
                        tortuga.setPosY(tPosY);
                        tortuga.setVelX(tVelX);
                        score = scr;
                        vidas = vid;
                        contadorVidas = contVid;
                        factorGravedad = fG;
                        factorAumento = fA;
                        factorAumentoY = fAY;
                        sonido = son;
                    } catch (IOException e) {
                        System.out.println("Error en " + e.toString());
                    }
                    cargar = false;
                } else {
                    cargar = false;
                }
                // Si se quiere guardar el juego (Asegurarse de que no este en instrucciones)
                if (guardar && !instrucciones) {
                    try {
                        grabaArchivo();
                    } catch (IOException e) {
                        System.out.println("Error en " + e.toString());
                    }
                    guardar = false;
                } else {
                    guardar = false;
                }

            }
        }

    }

    /**
     * Metodo usado para checar las colisiones del objeto tortuga y caparazon
     * con entre si <code>JFrame</code>.
     */
    public void checaColision() {
        // Si la tortuga esta colisionando con el caparazon
        if (tortuga.intersecta(caparazon)) {
            colision = true;
        }
        // Si el caparazon llega al suelo
        if (caparazon.getPosY() > getHeight()) {
            if (sonido) {
                shell.play();
            }
            contadorVidas--;
            volando = false;
            caparazon.setPosY(getHeight() / 2);
            caparazon.setPosX(0);
            caparazon.setVelX(0);
            caparazon.setVelY(0);
        }
        // Limitar a que la tortuga no pase de la mitad derecha a la izquierda
        if (tortuga.getPosX() < getWidth() / 2) {
            tortuga.setVelX(tortuga.getVelX() * -1);
            tortuga.setPosX(getWidth() / 2);
        }
        // Limitar a que la tortuga no se salga del frame
        if (tortuga.getPosX() > getWidth() - tortuga.getAncho()) {
            tortuga.setVelX(tortuga.getVelX() * -1);
            tortuga.setPosX(getWidth() - tortuga.getAncho());
        }
    }

    /**
     * Metodo <I>update</I> sobrescrito de la clase <code>JFrame</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor
     *
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     */
    public void paint(Graphics g) {
        // Inicializan el DoubleBuffer
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // Actualiza la imagen de fondo.
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Actualiza el Foreground.
        dbg.setColor(getForeground());
        paint1(dbg);

        // Dibuja la imagen actualizada
        g.drawImage(dbImage, 0, 0, this);
    }

    /**
     * Metodo <I>mouseClicked</I> sobrescrito de la interface
     * <code>MouseListener</code>.<P>
     * En este metodo maneja el evento que se genera cuando se hace click en el
     * ariados.
     *
     * @param e es el <code>evento</code> generado al presionar el ariados.
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Metodo <I>mouseEnteredd</I> sobrescrito de la interface
     * <code>MouseListener</code>.<P>
     * En este metodo maneja el evento que se genera cuando el ariados entra.
     *
     *
     * @param e es el <code>evento</code> generado al mover el ariados.
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Metodo <I>mouseExited</I> sobrescrito de la interface
     * <code>MouseListener</code>.<P>
     * En este metodo maneja el evento que se genera cuando el ariados sale.
     *
     *
     * @param e es el <code>evento</code> generado con el movimiento del
     * ariados.
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Metodo <I>mousePressed</I> sobrescrito de la interface
     * <code>MouseListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar el ariados.
     *
     *
     * @param e es el <code>evento</code> generado al presionar el ariados.
     */
    public void mousePressed(MouseEvent e) {
        // Si se presiona el caparazon y el objeto no ha sido picado antes, no esta volando ni pausado
        if (caparazon.intersecta(e.getX(), e.getY()) && !click && !volando && !pausado) {
            click = true;
            volando = true;
        }

    }

    /**
     * Metodo <I>mouseReleased</I> sobrescrito de la interface
     * <code>MouseListener</code>.<P>
     * En este metodo maneja el evento que se genera al solta el ariados.
     *
     *
     * @param e es el <code>evento</code> generado al soltar el ariados.
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Metodo <I>mouseMoved</I> sobrescrito de la interface
     * <code>MouseMotionListener</code>.<P>
     * En este metodo maneja el evento que se genera al mover el ariados
     *
     *
     * @param e es el <code>evento</code> generado al mover el ariados.
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Metodo <I>mouseDragged</I> sobrescrito de la interface
     * <code>MouseMotionListener</code>.<P>
     * En este metodo maneja el evento que se genera al mover el planeta una vez
     * seleccionado.
     *
     * @param e es el <code>evento</code> generado al mover el planeta.
     */
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Metodo <I>keyPressed</I> sobrescrito de la interface
     * <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar cualquier la
     * tecla.
     *
     * @param e es el <code>evento</code> generado al presionar las teclas.
     */
    public void keyPressed(KeyEvent e) {
        // Si se presiona P
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (pausado) {
                pausado = false;
            } else {
                pausado = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_I) { // Si se presiona I
            if (instrucciones) {
                instrucciones = false;
            } else {
                instrucciones = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A) {   // Si se presiona A (Debugging para comprobar click en el aire)
            if (!volando) {
                click = true;
            } else {
                click = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_S) {   // Si se presiona S
            if (!sonido) {
                sonido = true;
            } else {
                sonido = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {   // Si se presiona la flecha derecha
            if (!derecha) {
                derecha = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {    // Si se presiona la flecha izquierda
            if (!izquierda) {
                izquierda = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_C && !pausado) {   // Si se presiona C
            cargar = true;
        } else if (e.getKeyCode() == KeyEvent.VK_G && !pausado) {   // Si se presiona G
            guardar = true;
        }
    }

    /**
     * Metodo <I>keyTyped</I> sobrescrito de la interface
     * <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar una tecla que
     * no es de accion.
     *
     * @param e es el <code>evento</code> que se genera en al presionar las
     * teclas.
     */
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Metodo <I>keyReleased</I> sobrescrito de la interface
     * <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al soltar la tecla
     * presionada.
     *
     * @param e es el <code>evento</code> que se genera en al soltar las teclas.
     */
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {   // Cuando se deja de presionar la flecha izquierda
            if (izquierda) {
                izquierda = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {   // Cuando se deja de presionar la flecha derecha
            if (derecha) {
                derecha = false;
            }
        }
    }

    /**
     * Metodo que lee a informacion de un archivo y lo agrega a un vector.
     *
     * @throws IOException
     */
    public void leeArchivo() throws IOException {
        BufferedReader fileIn;
        try {
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        } catch (FileNotFoundException e) {
            // Crear un archivo demo
            File puntos = new File(nombreArchivo);
            PrintWriter fileOut = new PrintWriter(puntos);
            // Valores default
            fileOut.println("5,250,0,0,false,false,250,380,0,0,5,3,.5,1,1,true");
            fileOut.close();
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        }
        String dato = fileIn.readLine();

        while (dato != null) {
            // Leer el string, dividirlo y asignar a los auxiliares
            arr = dato.split(",");
            cPosX = (Integer.parseInt(arr[0]));
            cPosY = (Integer.parseInt(arr[1]));
            cVelX = (Float.parseFloat(arr[2]));
            cVelY = (Float.parseFloat(arr[3]));
            c = (Boolean.parseBoolean(arr[4]));
            v = (Boolean.parseBoolean(arr[5]));
            tPosX = (Integer.parseInt(arr[6]));
            tPosY = (Integer.parseInt(arr[7]));
            tVelX = (Float.parseFloat(arr[8]));
            scr = (Integer.parseInt(arr[9]));
            vid = (Integer.parseInt(arr[10]));
            contVid = (Integer.parseInt(arr[11]));
            fG = (Float.parseFloat(arr[12]));
            fA = (Float.parseFloat(arr[13]));
            fAY = (Float.parseFloat(arr[14]));
            son = (Boolean.parseBoolean(arr[15]));
            dato = fileIn.readLine();
        }
        fileIn.close();
    }

    /**
     * Metodo que agrega la informacion del vector al archivo.
     *
     * @throws IOException
     */
    public void grabaArchivo() throws IOException {
        PrintWriter fileOut = new PrintWriter(new FileWriter(nombreArchivo));
        // Guardar los valores necesarios para volver a cargar el juego en donde se quedo
        fileOut.println("" + caparazon.getPosX() + "," + caparazon.getPosY() + ","
                + caparazon.getVelX() + "," + caparazon.getVelY() + "," + click
                + "," + volando + "," + tortuga.getPosX() + "," + tortuga.getPosY()
                + "," + tortuga.getVelX() + "," + score + "," + vidas + ","
                + contadorVidas + "," + factorGravedad + "," + factorAumento + ","
                + factorAumentoY + "," + sonido);
        fileOut.close();
    }

    /**
     * Metodo paint que pinta todo en el <code>JFrame<code>.
     * @param g
     */
    public void paint1(Graphics g) {
        g.setColor(Color.black);
        if (!gameOver) {
            if (caparazon != null && tortuga != null) {
                // Mostrar background
                g.drawImage(background, 0, 0, this);
                // Mostrar puntuacion, vida y sonido
                g.drawString("Puntuacion: " + score, 5, 40);
                g.drawString("Vidas: " + vidas, 5, 60);
                String aux;
                if (sonido) {
                    aux = "Si";
                } else {
                    aux = "No";
                }
                g.drawString("Sonido: " + aux, 5, 80);
                // Si esta pausado, mostrarlo
                if (pausado) {
                    g.drawString("Pausado", getWidth() / 2, getHeight() / 2);
                }
                // Dibujar objetos inanimados
                g.drawImage(block, 7, 300, this);
                g.drawImage(plant, getWidth() / 2 - 60, getHeight() - 140, this);
                // Checar si el objeto esta en movimiento
                if (volando) {
                    g.drawImage(caparazon.getImagenI(), caparazon.getPosX(), caparazon.getPosY(), this);
                } else {
                    g.drawImage(im1, caparazon.getPosX(), caparazon.getPosY(), this);
                }
                // Checar si la tortuga esta en movimiento
                if (tortuga.getVelX() != 0) {
                    g.drawImage(tortuga.getImagenI(), tortuga.getPosX(), tortuga.getPosY(), this);

                } else {
                    g.drawImage(im2, tortuga.getPosX(), tortuga.getPosY(), this);
                }
                // Desplegar instrucciones
                if (instrucciones) {
                    g.setColor(Color.orange);
                    g.drawString(instr1, 20, 120);
                    g.drawString(instr2, 20, 140);
                    g.drawString(instr3, 20, 160);
                    g.drawString(instr4, 20, 180);
                }
            } else {
                //Da un mensaje mientras se carga el dibujo	
                g.drawString("No se cargo la imagen..", 20, 20);
            }
        } else {
            // Desplegar game over y credits
            g.drawImage(background, 0, 0, this);
            g.drawString("GAMEOVER", getWidth() / 2, getHeight() / 2);
            g.drawString("Hecho por:", 20, 120);
            g.drawString("Sergio Cordero", 20, 140);
            g.drawString("Bernardo Treviño", 20, 160);
            g.drawString("Tarea hecha para el profesor Mejorado. ¡Diviértase!", 20, 180);
        }

    }
}
