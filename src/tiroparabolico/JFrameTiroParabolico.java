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

    private Image dbImage;    // Imagen a proyectar
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
        
        derecha = false;
        izquierda = false;
        click = false;
        volando = false;

        setBackground(Color.white);
        setSize(800, 500);

        tortuga = new Tortuga(getWidth() / 2, (getHeight() - 50));
        caparazon = new Caparazon(5, getHeight() / 2);

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

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
        if (!pausado) {
            if (click && volando) {
                int opcion = (int) ((Math.random() * 15)) + 1; //da la opcion sobre la distancia final en x
                caparazon.setVelX(opcion);
                caparazon.setVelY(-20);
                click = false;
            }
            if (volando) {
                caparazon.gravedad();
                caparazon.setPosX(caparazon.getPosX() + caparazon.getVelX());
                caparazon.setPosY(caparazon.getPosY() + caparazon.getVelY());
            }
            if (caparazon.getPosY() > getHeight()) {
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

            tortuga.setPosX(tortuga.getPosX() + tortuga.getVelX());
            tortuga.gravedad();

            if (tortuga.getPosX() < 0) {
                tortuga.setVelX(tortuga.getVelX() * -1);
            }
            if (tortuga.getPosX() > getWidth() - tortuga.getAncho()) {
                tortuga.setVelX(tortuga.getVelX() * -1);
            }

            if (colision) {
                caparazon.setPosX(0);
                caparazon.setPosY(getHeight() / 2);
                caparazon.setVelX(0);
                caparazon.setVelY(0);
                volando = false;
                colision = false;
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
            fileOut.println("5,250,0,0,false,false,250,450,0");
            fileOut.close();
            fileIn = new BufferedReader(new FileReader(nombreArchivo));
        }
        String dato = fileIn.readLine();

        while (dato != null) {
            arr = dato.split(",");
            int cPosX = (Integer.parseInt(arr[0]));
            int cPosY = (Integer.parseInt(arr[1]));
            int cVelX = (Integer.parseInt(arr[2]));
            int cVelY = (Integer.parseInt(arr[3]));
            boolean c = (Boolean.parseBoolean(arr[4]));
            boolean v = (Boolean.parseBoolean(arr[5]));
            int tPosX = (Integer.parseInt(arr[6]));
            int tPosY = (Integer.parseInt(arr[7]));
            int tVelX = (Integer.parseInt(arr[8]));
            caparazon.setPosX(cPosX);
            caparazon.setPosY(cPosY);
            caparazon.setVelX(cVelX);
            caparazon.setVelY(cVelY);
            click = c;
            volando = v;
            tortuga.setPosX(tPosX);
            tortuga.setPosY(tPosY);
            tortuga.setVelX(tVelX);
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
        fileOut.println("" + caparazon.getPosX() + "," + caparazon.getPosY() + "," + caparazon.getVelX() + "," + caparazon.getVelY() + "," + click + "," + volando + "," + tortuga.getPosX() + "," + tortuga.getPosY() + "," + tortuga.getVelX());
        fileOut.close();
    }

    public void paint1(Graphics g) {
        if (caparazon != null && tortuga != null) {
            g.drawString("velocidad Y:" + caparazon.getVelY(), getWidth() / 2, 5);
            g.drawString("velocidad X:" + caparazon.getVelX(), getWidth() / 2, 15);
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
}
