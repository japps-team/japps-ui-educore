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
import japps.ui.component.Button;
import japps.ui.component.Dialogs;
import japps.ui.component.Media;
import japps.ui.component.ToggleButton;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import japps.ui.util.Log;
import japps.ui.util.Util;
import static japps.ui.util.Resources.*;
import japps.ui.util.Sound;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import static japps.ui.educore.object.Const.CHOOSE.*;
import java.awt.Component;

/**
 * Panel for choose activity
 * @author Williams Lopez - JApps
 */
public class ChooseActivityPanel extends ActivityPanel{
    
    JDialog mediaDialog;
    Media mediaPanel;
    ToggleButton[][] cards;

    /**
     * Creates new activity panel
     */
    public ChooseActivityPanel() {
        super();
        mediaPanel = new Media();
        mediaDialog = Dialogs.create(mediaPanel, "", true, null);
        mediaDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPanel.stop();
            }
            
        });
        
    }

    
    /**
     * Count all pending options
     * @return 
     */
    private int countPending(){
        int count= 0;
        for(ToggleButton[] l : cards){
            for(ToggleButton c: l){
                if(!c.isSelected()){
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * Launchs ann option media
     * @param button
     * @param option 
     */
    private void launchOption(ToggleButton button, ActivityOption option){
        
        button.setSelected(true);
        
        Path media = getMedia(option);
        int mediaType = getMediaType(option);
        String text = getText(option);
        String title = getTitle(option);
        
        if(text!=null && !text.isEmpty() && isSpeechText(option)){
            try {
                Path p =getSpeech(text);
                Sound.play(p);
            } catch (Exception e) {
                Log.debug("Cant play the sound in ChooseActivity",e);
            }
        }else{
            if(mediaType==Media.SOUND){
                mediaDialog.setSize(400, 100);
            }else if(mediaType==Media.IMAGE){
                mediaDialog.setSize(800, 650);
            }else{
                mediaDialog.setSize(800, 600);
            }
            mediaPanel.setMedia(media, mediaType);
            mediaDialog.setTitle(title);
            mediaPanel.updateUI();
            SwingUtilities.invokeLater(()->{mediaPanel.play();});
            mediaDialog.setVisible(true);
        }
        
        if(countPending()==0){
            getActivity().setState(Activity.COMPLETED);
        }
        
    }
    
    /**
     * builds this activity panel
     * @param activity
     */
    @Override
    protected void build(Activity activity){
        
        int rows = getRows(activity);
        int cols = getColumns(activity);
        int height = getThumbnailHeight(activity);
        int width = getThumbnailWidth(activity);
        
        
        if(rows*cols < activity.getOptions().size()){
            throw new RuntimeException("Cant place all "+activity.getOptions().size()+" options in matrix: [rows:"+rows+",cols:"+cols+"]");
        }
        
        cards = new ToggleButton[rows][cols];
        
        int r= 0, c=0;
        
        for(ActivityOption option : activity.getOptions()){
            Path thumb = getThumbnail(option);
            ToggleButton button = new ToggleButton();
            button.setCommand(option.getId());
            button.setAction((e)->{ 
                launchOption(button,option);
            });
            button.setText(getTitle(option));
            button.setVerticalTextPosition(Button.TOP);
            button.setHorizontalTextPosition(Button.CENTER);
            button.setHorizontalAlignment(Button.CENTER);
            button.setHorizontalAlignment(Button.CENTER);
            if(thumb!=null){
                button.setImage(Util.readImage(thumb),width,height);
            }
            
            cards[r][c] = button;
            c++;            
            if(c>=cols){
                c=0;
                r++;
            }
        }
        setComponents(cards);
        
    }


    @Override
    public void stop() {
        super.stop();
        mediaPanel.stop();
    }

    @Override
    public void release() {
        Sound.stop();
        mediaPanel.stop();
        mediaPanel.release();
    }
    
    
    
}
