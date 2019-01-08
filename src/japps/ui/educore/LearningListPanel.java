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
package japps.ui.educore;

import japps.ui.component.AccordionPanel;
import japps.ui.component.Button;
import japps.ui.component.Dialogs;
import japps.ui.component.Panel;
import japps.ui.educore.component.LearningPanel;
import japps.ui.educore.object.Const;
import japps.ui.educore.object.Learning;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

/**
 * A panel to show a list of Learnings
 *
 * @author Williams Lopez - JApps
 */
public class LearningListPanel extends AccordionPanel{
    
    private JFrame dialog;
    private List<Learning> learnings;
    

    /**
     * Current order 
     * @see ORDERED_NORMAL, ORDERED_BY_TOPIC
     */
    private boolean orderedByTopic = false;

    /**
     * Creates a new LearningListPanel instance
     */
    public LearningListPanel() {
    }

    /**
     * set the order of this list panel
     * @param orderedByTopic
     */
    public void setOrderedByTopic(boolean orderedByTopic) {
        this.orderedByTopic = orderedByTopic;
        setLearnings(learnings);
    }
    
    /**
     * Gets true if this panel is ordered by topic
     * @return 
     */
    public boolean isOrderedByTopic(){
        return this.orderedByTopic;
    }
    
    /**
     * Sets the learning list in this panel
     * @param learnings 
     */
    public void setLearnings(List<Learning> learnings){
        this.learnings = learnings;
        
        if(learnings == null) return;
        
        this.removeAll();
        
        if(orderedByTopic){
            HashMap<String,List<Learning>> groups = new HashMap<>();
            for(Learning l:learnings){
                String topicString = Const.LEARNING.getTopics(l);
                if(topicString != null){
                    String[] topics = topicString.split("[,]");
                    for(String topic:topics){
                        List<Learning> gr = groups.get(topic);
                        if(gr == null){
                            gr = new ArrayList<>();
                            gr.add(l);
                            groups.put(topic, gr);
                        }else{
                            if(!gr.contains(l)){
                                gr.add(l);
                            }
                        }
                    }
                }
            }
            groups.forEach((k,v)->{ addGroup(k, v); });
            
        }else{
            addGroup(Resources.$("All"), learnings);
        }
        
        setExpanded(true);
    }
    
    /**
     * Add a group of this list panel
     * @param name
     * @param learnings 
     */
    private void addGroup(String name,List<Learning> learnings){
        int rows = learnings.size()/5+1;
        int cols = 5;
        
        Component[][] comps = new Component[rows][cols];
        
        int r=0, c=0;
        for (Learning l: learnings){
            
            Button b = new Button();
            b.setHorizontalAlignment(Button.CENTER);
            b.setVerticalAlignment(Button.CENTER);
            b.setHorizontalTextPosition(Button.CENTER);
            b.setVerticalTextPosition(Button.TOP);
            b.setPreferredSize(new Dimension(170,170));
            b.setSize(b.getPreferredSize());
            b.setMaximumSize(b.getPreferredSize());
            b.setMinimumSize(b.getPreferredSize());
            b.addActionListener((e)->{ launchLearningInThread(l); });
            Image thumbnail = Util.readImage(Const.LEARNING.getThumbnail(l));
            b.setImage(thumbnail, 150, 150);
            b.setText(Const.LEARNING.getTitle(l));
            b.setBorder(BorderFactory.createEmptyBorder());
            comps[r][c] = b;
            
            c++;
            if(c>=cols){
                c=0;
                r++;
            }
            
        }
        addGroup(name, comps);
        
    }
    
    /**
     * Launches a learning in a new thread, displays a waiting gif
     * @param learning 
     */
    private void launchLearningInThread(Learning learning){
        Thread t = new Thread(()->{
            Dialogs.showWaiting(Resources.$("Loading learning..."));
            launchLearning(learning);
        });
        t.start();
    }
    
    /**
     * Launches the learning
     * @param learning 
     */
    private void launchLearning(Learning learning){
        
        
        if(dialog == null){
            dialog = new JFrame();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            dialog.setExtendedState(JFrame.MAXIMIZED_BOTH);
            dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            dialog.setBackground(Color.WHITE);
        }
        
        Panel container = new Panel();
        Button close = new Button();
        LearningPanel panel = new LearningPanel();
        
        close.setIcon("close.png", 25, 25);
        close.addActionListener((e)->{dialog.setVisible(false);});
        close.setHorizontalAlignment(Button.RIGHT);
        close.setVerticalAlignment(Button.CENTER);
        panel.setLearning(learning);
        container.setComponents(new Component[][]{
            {close},
            {panel}
        },
                new String[]{ Panel.FILL +","+ Panel.GROW},
                new String[]{ "25:25:25",Panel.FILL_GROW_CENTER });
        panel.start();
        
        dialog.setTitle(Const.LEARNING.getTitle(learning));
        dialog.setContentPane(container);
        Dialogs.hideWaiting();
        dialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("stopping");
                panel.stop();
            }
            
        });
        dialog.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentHidden(ComponentEvent e) {
                System.out.println("stopping");
                panel.stop();
            }
            
        });
        dialog.setVisible(true);
        if(Boolean.parseBoolean(Resources.p("app.learning.fullscreen"))){
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device=env.getDefaultScreenDevice();
            device.setFullScreenWindow(dialog);
        }else{
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device=env.getDefaultScreenDevice();
            device.setFullScreenWindow(null);
        }
    }
    
    
}
