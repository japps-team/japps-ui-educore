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
import japps.ui.component.Label;
import japps.ui.component.Panel;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityListener;
import japps.ui.educore.object.Const;
import japps.ui.educore.object.Learning;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Component;
import java.awt.Image;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import static japps.ui.educore.object.Const.LEARNING.*;

/**
 * This a panel to reproduce Activities
 * @author Williams Lopez - JApps
 */
public class LearningPanel extends Panel{
    
    private Button btnNext;
    private Button btnPrevious;
    private Panel  activityContainer;
    private Panel  controlsPanel;
    private Label  lblStatus;
    private Label  lblTitle;
    private Map<Activity,ActivityPanel> activityPanels;
    private ActivityPanel currentActivityPanel;

    
    private Activity current;
    private Learning learning;
    
    private int countActivities;
    
    
    

    /**
     * Constructs the Learning Panel
     */
    public LearningPanel() {
        btnNext = new Button((e) -> {nextActivity();});
        btnPrevious = new Button((e) -> {previousActivity();});
        activityContainer = new Panel();
        controlsPanel = new Panel();
        lblStatus = new Label();
        lblTitle = new Label();
        
        activityContainer.setBorder(BorderFactory.createEmptyBorder());
        setBorder(BorderFactory.createEmptyBorder());
        
        btnNext.setIcon("arrow-forward.png", 25, 25);
        btnPrevious.setIcon("arrow-back.png", 25, 25);
        
        controlsPanel.setComponents(new Component[][]{
            {lblTitle,lblStatus,btnPrevious,btnNext}
        },
         new String[]{Panel.GROW+","+Panel.LEFT, Panel.RIGHT, Panel.RIGHT, Panel.LEFT},
         new String[]{Panel.NONE});

        setComponents(new Component[][]{
            {activityContainer},
            {controlsPanel}
        },
                new String[]{Panel.FILL_GROW_CENTER},
                new String[]{Panel.FILL_GROW_CENTER,Panel.FILL+","+Panel.CENTER+",35:35:35"}
        );
        
        
        
    }

    /**
     * Sets the related learning with all info about its activities
     * @param learning 
     */
    public void setLearning(Learning learning) {
        this.learning = learning;
        this.activityPanels = new HashMap<>();
        if(this.learning != null && this.learning.getActivities() != null && !this.learning.getActivities().isEmpty()){
            
            DesktopApp.APP.setFullscreen(true);
            
            countActivities = 0;
            for(Activity a : learning.getActivities()){
                
                
                countActivities++;
                a.set("INDEX", countActivities);
                
                //Setting listener to the activity
                List<ActivityListener> listeners = a.getActivityListeners();
                boolean already = false;
                for(ActivityListener l : listeners){
                    if(l instanceof LearningPanel.LearningPanel_ActivitListener){
                        already = true;
                    }
                }
                if(!already){
                    a.addActivityListener(new LearningPanel_ActivitListener());
                }
                //Setting ActivityPanel
                
                try {
                    ActivityPanel panel;
                    String activityClass = a.get(Const.ACTIVITY_PANEL_CLASS);
                    Class c = Class.forName(activityClass);
                    panel = (ActivityPanel)c.newInstance();
                    panel.setActivity(a);
                    panel.setBorder(BorderFactory.createCompoundBorder());
                    activityPanels.put(a,panel);
                } catch (Exception err) {
                    Log.debug("Cant set all activities", err);
                    Dialogs.message(Resources.$("Error setting activities"), Resources.$("Cant set all activities: "+err.getMessage()), Resources.icon("warning.png", 100, 100));
                }
                
                    
            }
        }

        
    }
    
    /**
     * Shows next activity
     */
    private void nextActivity(){
        int id = 0;
        if(current != null) id = learning.getActivities().indexOf(current);
        if(id+1>=learning.getActivities().size()){
            refreshControlButtons();
            return;
        }else{
            id = (id+1);
        }
        Activity next = learning.getActivities().get(id);       
        next.set("previousId", current.getId());
        setActivity(next);
    }
    
    /**
     * Show previous activity
     */
    private void previousActivity(){
        if (current == null) return;
        String previousId = current.get("previousId");
        
        if (previousId == null) return;
        Activity previous = (Activity)learning.getActivityById(previousId);
        
        if(previous == null) return;
        
        setActivity(previous);
    }
    
    /**
     * Shows the currentActivity
     */
    private void setActivity(Activity activity){
        
        this.current = activity;
        //int currentId = learning.getActivities().indexOf(this.current);
        
        if(activity == null){
            Log.debug("Activity is null");
            return;
        }
        
        refreshControlButtons();
        
        if(currentActivityPanel != null){
            currentActivityPanel.stop();    
        }
        
        
        //String title = activity.get(Const.TITLE);
        try{
            int stateBefor = activity.getState();
            currentActivityPanel = activityPanels.get(activity);
            activityContainer.setComponents(new Component[][]{{currentActivityPanel}});

            if(currentActivityPanel.getActivity().getState()!=Activity.STARTED){
                currentActivityPanel.start();
            }
            activityContainer.repaint();
            
            
            if(stateBefor == Activity.NONSTARTED){
                SwingUtilities.invokeLater(()->{launchOnActivityStart();});
            }
            
        }catch(Exception e){
            Log.debug("Cant find class name", e);
        }
    }
    
    private void refreshControlButtons(){
        int currentId = 0;
        
        if(current != null){
            currentId = learning.getActivities().indexOf(current);
        }
        
        boolean prev = true;
        boolean next = true;
        
        if(Const.COMMON.isWaitUntilComplete(current) && current!=null && current.getState() != Activity.COMPLETED){
                next = false;
        }
        
        
        btnNext.setEnabled(next && (currentId<(learning.getActivities().size()-1)));
        btnPrevious.setEnabled(prev && currentId>0);
        
        
        if (current != null) {

            if (current.getState() == Activity.COMPLETED) {
                lblTitle.setText(Const.COMMON.getTitle(current) + " (" + Resources.$("Completed") + ")");
            } else {
                lblTitle.setText(Const.COMMON.getTitle(current));
            }

            lblStatus.setText(current.getInt("INDEX") + "/" + countActivities);
        }

    }
    
    private void launchOnActivityStart(){
        try {
            
            refreshControlButtons();
            
            ActivityPanel currentActivityPanel = LearningPanel.this.currentActivityPanel;
            
            String text = Const.COMMON.getText(currentActivityPanel.getActivity());

            if (Const.COMMON.isSpeechText(currentActivityPanel.getActivity())) {
                Resources.speech(text,false);
            }
            
            if((text!=null && !text.isEmpty())){
                Dialogs.message(Const.COMMON.getTitle(currentActivityPanel.getActivity()), text, Resources.icon("info.png", 100, 100));
            }

        } catch (Exception e) {
            Log.debug("Error on launchOnStart method", e);
        }
    }
    
     /**
     * Method launched when Activity is completed
     */
    private void launchOnActivityCompleted(){
        
        try {
            
            refreshControlButtons();
            
            ActivityPanel currentActivityPanel = LearningPanel.this.currentActivityPanel;
            
            String text = Const.COMMON.getFinalText(currentActivityPanel.getActivity());

            if (Const.COMMON.isSpeechText(currentActivityPanel.getActivity())) {
                Resources.speech(text,false);
            }
            
            Image image = null;
            
            try {
                Path p= Const.COMMON.getSuccessImage(currentActivityPanel.getActivity());
                if(p!=null){
                    image = Util.readImage(p).getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                }
            } catch (Exception e) {
                Log.debug("Cant find successImage ",e);
            }
            
            if((text!=null && !text.isEmpty()) || image!=null){
                Dialogs.message(Const.COMMON.getTitle(currentActivityPanel.getActivity()),text,image);
            }
            
            if(Const.COMMON.isWaitUntilComplete(currentActivityPanel.getActivity())){
                nextActivity();
            }

        } catch (Exception e) {
            Log.debug("Error on launchOnCompleted method", e);
        }
        
    }
    
    /**
     * Starts the learning panel
     */
    public void start(){
        if(learning !=null && learning.getActivities()!=null && !learning.getActivities().isEmpty()){
            setActivity(learning.getActivities().get(0));
        }
    }
    
    public void stop(){
        if(currentActivityPanel!=null){
            currentActivityPanel.stop();
        }
    }
    
    
    public class LearningPanel_ActivitListener implements ActivityListener {

        @Override
        public void state(int state) {
            if(state == Activity.COMPLETED){
                launchOnActivityCompleted();
            }
        }
    }
    
    
}
