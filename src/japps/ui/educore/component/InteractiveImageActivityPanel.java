/*
 * Copyright (C) 2018 Williams Lopez - JApps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package japps.ui.educore.component;

import japps.ui.DesktopApp;
import japps.ui.component.Dialogs;
import japps.ui.component.Media;
import japps.ui.component.ToggleButton;
import japps.ui.component.action.AbstractMouseListener;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import japps.ui.educore.object.Const;
import japps.ui.util.Util;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import static japps.ui.educore.object.Const.INTERACTIVE_IMAGE.*;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Sound;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

/**
 *
 * @author Williams Lopez - JApps
 */
public class InteractiveImageActivityPanel extends ActivityPanel{
    
    private Image image;
    private Media mediaComponent;
    private JDialog dialog;
    private ToggleButton[][] matrix;
    Image buttonImage;
    Image buttonImage2;

    public InteractiveImageActivityPanel() {
        matrix = new ToggleButton[40][40];
        mediaComponent = new Media();
        buttonImage = Resources.icon("add-circle.png",20,20);
        buttonImage2 = Resources.icon("done.png",20,20);
    }
    
    
    
        @Override
    protected void paintComponent(Graphics g) {
        if(isShowing() && image!=null){
            Image scaled = image;
            int w = getWidth()-(getInsets().left+getInsets().right);
                int h = getHeight()-(getInsets().top+getInsets().bottom);                
                scaled = image.getScaledInstance(w, h, Image.SCALE_FAST);
            g.drawImage(scaled, getInsets().left, getInsets().top, this);
        }
        super.paintComponent(g);
    }
    

    @Override
    protected void build(Activity activity) {
        
        //cleaning matrix
        for(ToggleButton[] l:matrix){
            for(int i=0;i<l.length;i++){ l[i]=null; }
        }
        
        if(activity == null || activity.getOptions() == null){
            image = null;
            setComponents(matrix);
            return;
        }
        
        image = Util.readImage(getImage(activity));
        
            
        for(ActivityOption o : activity.getOptions()){
            int r = getRow(o);
            int c = getColumn(o);
            String title = getTitle(o);
            ToggleButton b = new ToggleButton();
            b.setHorizontalAlignment(ToggleButton.CENTER);
            b.setVerticalAlignment(ToggleButton.CENTER);
            b.setImage(buttonImage);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setToolTipText(title);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setCommand(o.getId());
            b.setAction((e)->{ launchOption(e);});
            b.addMouseListener(new AbstractMouseListener(){
                @Override
                public void mouseEntered(MouseEvent e) {
                    speechText(o);
                }
            });
            matrix[r][c] = b;
        }
        
        setComponents(matrix);
    }
    
    private void speechText(ActivityOption o){
        Sound.stop();
        if(isSpeechText(o)){
        String text = getText(o);       
            if(text != null){
                try {
                    Sound.play(Resources.getSpeech(text));
                } catch (Exception e) {
                    Log.debug("Cant play text speech: "+text);
                }
            }
        }
    }
    
    private void launchOption(ActionEvent e){
        
        ToggleButton b = (ToggleButton)e.getSource();
        
        b.setImage(buttonImage2);
        
        b.setSelected(true);
        
        ActivityOption option = getActivity().getOptionById(e.getActionCommand());
        Path media = getMedia(option);
        int mediaType = getMediaType(option);
        String title = getTitle(option);
        mediaComponent.setMedia(media, mediaType);
        
        if(dialog == null){
            dialog = Dialogs.create(mediaComponent, title , true, null);
            
        }
        
        switch(mediaType){
            case Media.VIDEO: case Media.IMAGE: dialog.setSize(800, 650); break;
            case Media.SOUND: dialog.setSize(400, 100); break;
                
        }
            
        dialog.setVisible(true);
        
        if(countPending() == 0){
            getActivity().setState(Activity.COMPLETED);
        }
        
    }
    
    private int countPending(){
        int count = 0;
        for(ToggleButton[] l : matrix){
            for(ToggleButton b:l){
                if(b!=null && !b.isSelected()){
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void release() {
        mediaComponent.release();
    }


    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public void stop() {
        super.stop();
        mediaComponent.stop();
    }
    
    
    
    
    
    
}
