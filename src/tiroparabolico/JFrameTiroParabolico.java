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
 *
 * @author bernardot
 */
public class JFrameTiroParabolico extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    private Caparazon caparazon; //objeto del caparazon
    private Tortuga tortuga; //objeto de la tortuga

    private int score;
    private int vidas;
    private int contadorVidas;

    private float factorGravedad;
    private float factorAumento;

    private Image dbImage;    // Imagen a proyectar
    private Image background;
    private Graphics dbg;   // Objeto grafico
    private boolean pausado;    // Valor booleano para saber si el JFrame esta en pausa
    private boolean instrucciones;  // Valor booleano para mostrar/quitar instrucciones
    private boolean cargar;
    private boolean guardar;
    private String instr1; // String que contiene las instrucciones del juego.
    private String instr2; // String que contiene las instrucciones del juego.
    private String instr3;
    private SoundClip shell;
    private SoundClip catched;
    private String nombreArchivo;   // Nombre del archivo
    private String[] arr;   // Arreglo del archivo dividido

    // banderas
    // de movimiento del caparazon
    private boolean click;
    private boolean volando;
    //de movimiento de la tortuga
    private boolean derecha;
    private boolean izquierda;

    private boolean gameOver;

    private String instr; // String que contiene las instrucciones del juego.
    private Vector vec;     // Objeto vector

    private boolean colision;

    public JFrameTiroParabolico() {
        //Se inicializan variables

        pausado = false;
        instrucciones = false;
        // TODO Corregir
        instr1 = "El juego consiste en intentar atrapar la pelota con la canasta. Al momento de darle clic a la pelota, esta se "; // Instrucciones del juego
        instr2 = "moverá a través de la pantalla. Con las teclas izquierda y derecha, podrás mover la canasta. Si no llegas ";
        instr3 = "atrapar la pelota, la pelota caerá más rápido... ¡CUIDADO! ¡TU PUEDES!";
        // END TODO Corregir
        // Se cargan los sonidos
        shell = new SoundClip("sounds/stomp.wav");
        catched = new SoundClip("sounds/marioSound.wav");

        nombreArchivo = "Datos.txt";
        cargar = false;
        guardar = false;

        score = 0;
        vidas = 5;
        contadorVidas = 3;
        gameOver = false;

        factorGravedad = (float) .5;
        factorAumento = 1;

        derecha = false;
        izquierda = false;
        click = false;
        volando = false;

        URL bURL = this.getClass().getResource("/images/images.gif");
        background = Toolkit.getDefaultToolkit().getImage(bURL);

        setSize(800, 500);

        tortuga = new Tortuga(getWidth() / 2, (getHeight() - 120));
        caparazon = new Caparazon(5, getHeight() / 2);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        // TODO
        instr = "El juego consiste en intentar atrapar al caparazon con la tortuga. Al momento de darle clic al caparazon, este se moverá a través de la pantalla. Con las teclas izquierda y derecha, podrás mover la tortuga. Si no llegas atrapar el caparazon, el caparazon caerá más rápido... ¡CUIDADO! ¡TU PUEDES!"; // Instrucciones del jugo
        // END TODO Corregir
        // Se cargan los sonidos
        shell = new SoundClip("sounds/stomp.wav");
        catched = new SoundClip("sounds/marioSound.wav");

        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    public void run() {

        while (true) {
            if (!pausado) {
                actualiza();
                checaColision();
            }

            // Se actualiza el <code>Applet</code> repintando el contenido.
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
     * Metodo usado para actualizar la posicion de objetos heroe y ariados.
     *
     */
    public void actualiza() {
        //Determina el tiempo que ha transcurrido desde que el Applet inicio su ejecución
        if (!gameOver) {
            if (!pausado) {
                if (click && volando) {
                    int opcion = (int) ((Math.random() * 8)) + 1; //da la opcion sobre la distancia final en x
                    caparazon.setVelX(opcion * factorAumento);
                    caparazon.setVelY(-15);
                    click = false;
                }
                if (contadorVidas < 1) {
                    vidas--;
                    factorGravedad += 0.5;
                    factorAumento += 0.8;
                    contadorVidas = 3;
                }

                if (vidas == 0) {
                    gameOver = true;
                }
                if (volando) {
                    caparazon.gravedad(factorGravedad);
                    int x = (int) Math.round(caparazon.getVelX());
                    int y = (int) Math.round(caparazon.getVelY());
                    caparazon.setPosX(caparazon.getPosX() + x);
                    caparazon.setPosY(caparazon.getPosY() + y);
                }

                if (caparazon.getPosY() > getHeight()) {
                    shell.play();
                    contadorVidas--;
                    volando = false;
                    caparazon.setPosY(getHeight() / 2);
                    caparazon.setPosX(0);
                    caparazon.setVelX(0);
                    caparazon.setVelY(0);
                }
                if (izquierda) {
                    tortuga.setVelX(tortuga.getVelX() - 2);
                }
                if (derecha) {
                    tortuga.setVelX(tortuga.getVelX() + 2);
                }

                int x = (int) Math.round(tortuga.getVelX());
                tortuga.setPosX(x + tortuga.getPosX());
                tortuga.gravedad();

                if (tortuga.getPosX() < 0) {
                    tortuga.setVelX(tortuga.getVelX() * -1);
                }
                if (tortuga.getPosX() > getWidth() - tortuga.getAncho()) {
                    tortuga.setVelX(tortuga.getVelX() * -1);
                }

                if (colision) {
                    catched.play();
                    caparazon.setPosX(0);
                    caparazon.setPosY(getHeight() / 2);
                    caparazon.setVelX(0);
                    caparazon.setVelY(0);
                    volando = false;
                    colision = false;
                    score += 2;
                }
                if (cargar) {
                    try {
                        leeArchivo();
                    } catch (IOException e) {
                        System.out.println("Error en " + e.toString());
                    }
                    cargar = false;
                }

                if (guardar) {
                    try {
                        grabaArchivo();
                    } catch (IOException e) {
                        System.out.println("Error en " + e.toString());
                    }
                    guardar = false;
                }

            }
        }

    }

    /**
     * Metodo usado para checar las colisiones del objeto tortuga y caparazon
     * con entre si <code>Applet</code>.
     */
    public void checaColision() {
        if (tortuga.intersecta(caparazon)) {
            colision = true;
        }
    }

    /**
     * Metodo <I>update</I> sobrescrito de la clase <code>Applet</code>,
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
        if (caparazon.intersecta(e.getX(), e.getY()) && !click && !volando) {
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
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (pausado) {
                pausado = false;
            } else {
                pausado = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_I) {
            if (instrucciones) {
                instrucciones = false;
            } else {
                instrucciones = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            if (!click) {
                click = true;
            } else {
                click = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (!derecha) {
                derecha = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (!izquierda) {
                izquierda = true;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_C) {
            cargar = true;
        } else if (e.getKeyCode() == KeyEvent.VK_G) {
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
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (izquierda) {
                izquierda = false;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
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
            File puntos = new File(nombreArchivo);
            PrintWriter fileOut = new PrintWriter(puntos);
            fileOut.println("100,demo");
            fileOut.close();
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        }
        String dato = fileIn.readLine();

        while (dato != null) {
            arr = dato.split(",");
            int num = (Integer.parseInt(arr[0]));
            String nom = arr[1];
            //vec.add(new Puntaje(nom, num));
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
        for (int i = 0; i < vec.size(); i++) {
            //Puntaje x;
            //x = (Puntaje) vec.get(i);
            //fileOut.println(x.toString());
        }
        fileOut.close();
    }

    public void paint1(Graphics g) {
        g.setColor(Color.black);
        if (!gameOver) {
            if (caparazon != null && tortuga != null) {
                //g.drawImage(background, 0, 0, this);
                g.drawString("Puntuacion: " + score, 5, 50);
                g.drawString("Vidas: " + vidas, 5, 70);
                g.drawImage(caparazon.getImagenI(), caparazon.getPosX(), caparazon.getPosY(), this);
                g.drawImage(tortuga.getImagenI(), tortuga.getPosX(), tortuga.getPosY(), this);
                if (instrucciones) {
                    g.setColor(Color.BLACK);
                    g.drawString(instr1, 20, 80);
                    g.drawString(instr2, 20, 100);
                    g.drawString(instr3, 20, 120);
                }
            } else {
                //Da un mensaje mientras se carga el dibujo	
                g.drawString("No se cargo la imagen..", 20, 20);
            }
        }
        else {
            g.drawString("GAMEOVER", getWidth()/2, getHeight()/2);
        }

    }
}
