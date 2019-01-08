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

import japps.ui.component.Button;
import japps.ui.component.Panel;
import japps.ui.component.ToggleButton;
import japps.ui.component.action.AbstractMouseListener;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import japps.ui.educore.object.Const;
import japps.ui.util.Util;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;
import static japps.ui.educore.object.Const.DND.*;
import japps.ui.util.Dnd;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Sound;
import java.awt.Color;
import java.awt.ScrollPane;
import java.nio.file.Path;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author Williams Lopez - JApps
 */
public class DnDActivityPanel extends ActivityPanel{
    
    
    private Image image;
    private Panel leftPanel;
    private Panel rightPanel;
    private TB_ActivityOption_Answer[][] answers;
    

    public DnDActivityPanel() {
        super();
        leftPanel = new Panel();
        rightPanel = new Panel();
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(leftPanel);
        scroll.setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.white);
        setComponents(new Component[][]{{scroll,rightPanel}}, 
                new String[]{"250:250:250,"+Panel.FILL,Panel.FILL_GROW_CENTER}, new String[]{Panel.FILL_GROW_CENTER});
    }
    
    

    @Override
    protected void build(Activity activity) {
        
        int rows = getRows(activity);
        int columns = getColumns(activity);
        
        answers = new TB_ActivityOption_Answer[rows][columns];
        Image back = Resources.icon("image.png");
        int w = getThumbnailWidth(activity);
        int h = getThumbnailHeight(activity);
        
        for(ActivityOption o : activity.getOptions()){
            int row = getRow(o);
            int col = getColumn(o);
            TB_ActivityOption_Answer b = new TB_ActivityOption_Answer();
            b.setCorrectOption(o);
            b.setDndMode(Dnd.DROPPABLE);
            b.setImage(back, w, h);
            b.setDropAction((dragged,to)->{
                //System.out.println("Pending:"+countPending());
                TB_ActivityOption o1 = (TB_ActivityOption)dragged;
                TB_ActivityOption o2 = (TB_ActivityOption)to;
                o2.setOption(o1.getOption());
                
                if(countPending()<=0){
                    getActivity().setState(Activity.COMPLETED);
                }
                
                System.out.println("5");
                
            });
            answers[row][col] = b;
        }
        rightPanel.setComponents(answers);
        
        
        TB_ActivityOption[][] compsLeft = new TB_ActivityOption[activity.getOptions().size()][1];
        ActivityOption[] mixed = mix(activity.getOptions());
        int i=0;
        for(ActivityOption o : mixed){
            TB_ActivityOption b = new TB_ActivityOption();
            b.setOption(o);
            b.setDndMode(Dnd.DRAGGABLE);
            b.addActionListener((e)->{
                    String text = Const.DND.getText(o);
                    Resources.speech(text,false);
            });
            compsLeft[i][0]= b;
            i++;
        }
        
        leftPanel.setComponents(compsLeft);
        
    }
    
    private ActivityOption[] mix(List<ActivityOption> options){
        ActivityOption[] mixed = new ActivityOption[options.size()];
        for(ActivityOption o : options){
            int pos;
            do{
                pos = (int)(Math.random()*mixed.length);
            }while(mixed[pos]!=null);
            mixed[pos] = o;
        }
        return mixed;
    }
    
    int countPending(){
        int count = 0;
        for(TB_ActivityOption_Answer[] l: answers){
            for(TB_ActivityOption_Answer a:l){
                if(!a.answerMatch()){
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void release() {
        
    }
    
    
    
    class TB_ActivityOption extends Button{
        
        ActivityOption option;

        public TB_ActivityOption() {
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setHorizontalTextPosition(Button.CENTER);
            setVerticalTextPosition(Button.TOP);
        }


        public ActivityOption getOption() {
            return option;
        }

        public void setOption(ActivityOption option) {
            this.option = option;
            String title = getTitle(option);
            Image image = Util.readImage(getThumbnail(option));
            setText(title);
            setImage(image,200,100);
        }
        
    }
    
    class TB_ActivityOption_Answer extends TB_ActivityOption{
        
        private ActivityOption correctOption;

        public TB_ActivityOption_Answer() {
            super();
        }
        
        

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (option != null && correctOption != null) {
                if (correctOption.equals(option)) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(Color.red);
                }
                g.fillOval(getWidth() - 30, 10, 20, 20);
            }
        }



        public ActivityOption getCorrectOption() {
            return correctOption;            
        }

        public void setCorrectOption(ActivityOption correctOption) {
            this.correctOption = correctOption;
        }

        @Override
        public void setOption(ActivityOption option) {
            this.option = option;
            String title = getTitle(option);
            int w = getThumbnailWidth((Activity)option.getParent());
            int h = getThumbnailHeight((Activity)option.getParent());
            Image image = Util.readImage(getThumbnail(option));
            setText(title);
            setImage(image,w,h);
        }
        
        public boolean answerMatch(){
            if(option == null || correctOption ==null){
                return false;
            }
            return option.equals(correctOption);
        }
        
    }
    
    
}
