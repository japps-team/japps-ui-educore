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

import japps.ui.component.Label;
import japps.ui.component.Media;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import japps.ui.educore.object.Const;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Sound;
import java.nio.file.Path;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import static japps.ui.educore.object.Const.READ.*;
import java.awt.Color;
import java.awt.Graphics;


/**
 * A component ui for the ReadActivity 
 * @author Williams Lopez - JApps
 */
public class ReadActivityPanel extends ActivityPanel{

    
    private Label lbText;
    private Media mediaComponent;
    private Timer timerNext;
    private int currentOption=0;
    private double progress = 0;
    private Timer timerProgress;
    private int progressCount = 0;

    public ReadActivityPanel() {
        super();
        timerNext = new Timer(100000000, (e)->{  next(); progressCount = 0; });
        timerNext.setRepeats(false);
        
        timerProgress = new Timer(1000,(e)->{ calculateProgress();});
        timerProgress.setRepeats(true);
        
        
        lbText = new Label();
        lbText.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mediaComponent = new Media();
        mediaComponent.setMinimumSize(new Dimension(700, 100));
    }

    
    private void calculateProgress(){
        
        try{
            
            if(getActivity()==null || getActivity().getOptions()==null || currentOption>=getActivity().getOptions().size()){
                return;
            }
            
            progressCount ++;
            progress =  ((double)currentOption)/((double)getActivity().getOptions().size());
            double portion = 1d/((double)getActivity().getOptions().size());
            double timeToShow= (double)getTimeToShow(getActivity().getOptions().get(currentOption))/1000;
            double progressOption = progressCount/timeToShow;
            progressOption = (progressOption>1)?1:progressOption;
            progress += portion*progressOption;
        }catch(Throwable err){
            Log.debug("Cant calculate progress in read activity",err);
        }
                
        
        
        ReadActivityPanel.this.repaint();
    }
    
    /**
     * Sets the mode
     * @param showText
     * @param showMedia 
     */
    private void mode(boolean showText,boolean showMedia){
        removeAll();
        if(showText && showMedia){
            setComponents(new Component[][]{
                {lbText,mediaComponent},
            });
        }else if(showText){
            setComponents(new Component[][]{
                {lbText}
            });
        }else{
            setComponents(new Component[][]{
                {mediaComponent}
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        g.clearRect(0, 0, getWidth(), 10);
        int w = (int)(getWidth()*progress);
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, w, 5);
        
    }
    
    
    

    /**
     * Next read activity
     */
    private void next(){
        currentOption++;
        setCurrentActivityOption();
    }
    
    private void previous(){
        currentOption--;
        setCurrentActivityOption();
    }
    
    private void setCurrentActivityOption(){
        
        this.setVisible(false);
        
        if(getActivity() == null || getActivity().getOptions() == null || getActivity().getOptions().isEmpty()){
            return;
        }
        
        
        if(currentOption>=getActivity().getOptions().size()){
            if(mediaComponent != null) mediaComponent.stop();
            Sound.stop();
            getActivity().setState(Activity.COMPLETED);
            return;
        }else if(currentOption<0){
            currentOption = 0;
        }

        ActivityOption option = getActivity().getOptions().get(currentOption);
        
        try {
            
            Sound.stop();
            mediaComponent.stop();
            
            mode(isShowText(option),isShowMedia(option));
            
            String text = getText(option);
            Path media = getMedia(option);
            int media_type = getMediaType(option);
            int timeToShow = getTimeToShow(option);
            Font font = Const.READ.getFont(option);
            boolean speechText = isSpeechText(option);

            
            if(media!=null){
                mediaComponent.setMedia(media,media_type);
            }            

            //start the timer
            
            Log.debug("Setting timer delay to: "+timeToShow);
            timerNext.setInitialDelay(timeToShow);
            timerNext.start();

            
            lbText.setText(((text!=null)?"<html>"+text+"</html>":""));
            lbText.setFont(font);
            if(speechText && text!=null && !text.isEmpty()){
                Resources.speech(text);
            }

            
            this.setVisible(true);
            if(media_type != Media.IMAGE){
                SwingUtilities.invokeLater(()->{mediaComponent.play();});
            }
            
        } catch (Throwable err) {
            Log.debug("Error al mostrar la opcion en ReadActivityPanel", err);
        }
        
        
        
    }

    @Override
    public void start() {
        super.start();
        currentOption = 0;
        setCurrentActivityOption();
        timerProgress.start();
    }


    @Override
    public void stop() {
        super.stop();
        mediaComponent.stop();
        timerNext.stop();
        timerProgress.stop();
    }

    @Override
    public void release() {
        if(mediaComponent != null){
            try {
                mediaComponent.release();
                Sound.stop();
            } catch (Exception e) {
            }
        }
        timerProgress.stop();
    }

    @Override
    protected void build(Activity activity) {
        //no extra build needed
    }
    
    
}
