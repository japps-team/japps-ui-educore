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
import japps.ui.component.Panel;
import japps.ui.educore.object.Activity;
import static japps.ui.educore.object.Const.MEMORY.getSuccessImage;
import static japps.ui.educore.object.Const.MEMORY.getText;
import static japps.ui.educore.object.Const.MEMORY.getTitle;
import static japps.ui.educore.object.Const.MEMORY.isSpeechText;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Sound;
import japps.ui.util.Util;
import java.awt.Image;
import java.nio.file.Path;

/**
 *
 * @author Williams Lopez - JApps
 */
public abstract class ActivityPanel extends Panel{
    
    
    private Activity activity;


    /**
     * Constructs a new Activity Panel
     */
    public ActivityPanel() {
        super();
        Log.debug("Creating an activity panel: "+getClass().getName());
        
    }
    

    /**
     * Gets the related activity
     * @return 
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity, after seting the property activity, the abstract method build(Activity activity) is invoked
     * @param activity 
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        if(activity == null || activity.getOptions()==null || activity.getOptions().isEmpty()){
            return;
        }
        build(activity);
        activity.setState(Activity.NONSTARTED);
    }

    /**
     * Get the activity state
     * @see Activity
     * @return 
     */
    public int getState(){
        if(activity!=null) return activity.getState();
        return 0;
    }
    
    /**
     * Starts the activity panel, if a video is showing in the activity panel then the video is going to play
     * Overwrite if any start code is required
     */
    public void start(){
        Sound.stop();
        if(getActivity().getState() != Activity.COMPLETED){
            getActivity().setState(Activity.STARTED);
        }
    }
    
    /**
     * Restarts the activity the state is changed from COMPLETE to PLAYING
     * Method setActivity is called
     * Overwrite if any restart code is required
     */
    public void restart(){
        stop();
        setActivity(getActivity());
        
    }
    
    
    /**
     * Stops the activity panel
     * Overwrite for any additional code required
     */
    public void stop(){
        Sound.stop();
    }
    
    /**
     * Builds this ActivityPanel component, this abstract method is invoked inside setActivity
     * @param activity
     */
    protected abstract void build(Activity activity);

    /**
     * Release all activity sources
     */
    public abstract void release();
    
    
    
    
}
