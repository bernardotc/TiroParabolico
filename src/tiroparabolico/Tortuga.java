/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiroparabolico;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Toolkit;

/**
 *
 * @author mrquorr
 */
public class Tortuga extends Base{
    /**
	 * Metodo constructor que hereda los atributos de la clase <code>Base</code>.
	 * @param posX es la <code>posiscion en x</code> del objeto tortuga.
	 * @param posY es el <code>posiscion en y</code> del objeto tortuga.
    **/
	public Tortuga(int posX,int posY){
            super(posX,posY);
            this.velX = 0;
            this.velY = 0;
            Image imagen = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("images/koopa.gif"));
            ImageIcon image = new ImageIcon(imagen);
            this.setImageIcon(image);
        }
        
        public void gravedad(){
            if (this.velX < 0){
                this.velX += 1;
            }
            else if (this.velX > 0){
                this.velX -= 1;
            }
        }
}
