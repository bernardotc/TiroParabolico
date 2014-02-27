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

/**
 *
 * @author bernardot
 */
public class JFrameTiroParabolico extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    private Caparazon caparazon; //objeto del caparazon
    private Tortuga tortuga; //objeto de la tortuga

    private Image dbImage;    // Imagen a proyectar
    private Graphics dbg;   // Objeto grafico
    private boolean pausado;    // Valor booleano para saber si el JFrame esta en pausa
    private boolean instrucciones;  // Valor booleano para mostrar/quitar instrucciones

    // banderas
        // de movimiento del caparazon
    private boolean click;
    private boolean volando;
        //de movimiento de la tortuga
    private boolean derecha;
    private boolean izquierda;
    
    private boolean colision;
    
    private String instr; // String que contiene las instrucciones del juego.

    public JFrameTiroParabolico() {
        //Se inicializan variables
        pausado = false;
        instrucciones = false;
        
        derecha = false;
        izquierda = false;
        click = false;
        volando = false;

        setBackground(Color.white);
        setSize(800, 500);

        tortuga = new Tortuga(getWidth()/2, (getHeight() - 50));
        caparazon = new Caparazon(5, getHeight() / 2);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        // TODO
        instr = "El juego consiste en..."; // Instrucciones del jugo
        start();
        // END TODO
    }
    
    /**
     * Metodo <I>start</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo para la animacion este metodo
     * es llamado despues del init o cuando el usuario visita otra pagina y
     * luego regresa a la pagina en donde esta este <code>Applet</code>
     *
     */
    public void start() {
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
            if (caparazon.getPosY() > getHeight()){
                volando = false;
                caparazon.setPosY(getHeight()/2);
                caparazon.setPosX(0);
                caparazon.setVelX(0);
                caparazon.setVelY(0);
            }
            if (izquierda){
                tortuga.setVelX(tortuga.getVelX() - 2);
            }
            if (derecha){
                tortuga.setVelX(tortuga.getVelX() + 2);
            }
            
            tortuga.setPosX(tortuga.getPosX() + tortuga.getVelX());
            tortuga.gravedad();
            
            if (tortuga.getPosX() < 0){
                tortuga.setVelX(tortuga.getVelX() * -1);
            }
            if (tortuga.getPosX() > getWidth() - tortuga.getAncho()){
                tortuga.setVelX(tortuga.getVelX() * -1);
            }
            
            if (colision){
                caparazon.setPosX(0);
                caparazon.setPosY(getHeight()/2);
                caparazon.setVelX(0);
                caparazon.setVelY(0);
                volando = false;
                colision = false;
            }
            
            
        }

    }

    /**
     * Metodo usado para checar las colisiones del objeto tortuga y caparazon con
     * entre si <code>Applet</code>.
     */
    public void checaColision() {
        if (tortuga.intersecta(caparazon)){
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

    public void paint1(Graphics g) {
        if (caparazon != null && tortuga != null) {
            g.drawString("velocidad Y:" + caparazon.getVelY(), getWidth()/2, 5);
            g.drawString("velocidad X:" + caparazon.getVelX(), getWidth()/2, 15);
            g.drawImage(caparazon.getImagenI(), caparazon.getPosX(), caparazon.getPosY(), this);
            g.drawImage(tortuga.getImagenI(), tortuga.getPosX(), tortuga.getPosY(), this);
            if (instrucciones) {
                g.drawString(instr, 20, 20);
            } else {

            }
        } else {
            //Da un mensaje mientras se carga el dibujo	
            g.drawString("No se cargo la imagen..", 20, 20);
        }
    }
}
