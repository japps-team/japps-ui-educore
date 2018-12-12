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
import japps.ui.component.Label;
import japps.ui.component.Panel;
import japps.ui.component.ToggleButton;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import static japps.ui.educore.object.Const.CONNECT.*;
import static japps.ui.educore.object.Const.MEMORY.getSuccessImage;
import static japps.ui.educore.object.Const.MEMORY.getText;
import static japps.ui.educore.object.Const.MEMORY.getTitle;
import static japps.ui.educore.object.Const.MEMORY.isSpeechText;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Image;
import java.nio.file.Path;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *
 * @author Williams Lopez - JApps
 */
public class ConnectActivityPanel extends ActivityPanel{

    private int cardHeight;
    private int cardWidth;
    private List<Card> leftCards;
    private List<Card> rightCards;
    private Card lastCard;
    
    private List<Card[]> errors = new ArrayList<>();
    
    
    private Color lineColor = Color.YELLOW;
    

    
    public ConnectActivityPanel() {
    }
    
    

    private void createCards(Activity a){
        leftCards = new ArrayList<>();
        rightCards = new ArrayList<>();
        int id = 0;
        for(ActivityOption o : a.getOptions()){
            if(getPairOptionId(o)!=null){
                Card card = createCard(o);
                Card pair = createCard(getPair(o));
                card.pair = pair;
                card.left = true;
                pair.pair = card;
                pair.left = false;
                leftCards.add(card);
                rightCards.add(pair);
                id++;
            }
        }
    }
    
    private Card createCard(ActivityOption o){
        final Card card = new Card();
        card.button = new ToggleButton();
        card.completed = false;
        card.frontImage = Util.readImage(getThumbnail(o)).getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
        card.option = o;
        card.button.setImage(card.frontImage);
        card.button.setBorder(BorderFactory.createEmptyBorder());
        card.button.setAction((e)->{ selectCard(card); });
        card.setEnable(true);
        return card;
    }
      
    private ActivityOption getPair(ActivityOption option){
        
        if(getPairOptionId(option)!=null){
            return getActivity().getOptionById(getPairOptionId(option));
        }else{
            for(ActivityOption o: getActivity().getOptions()){
                String pair = getPairOptionId(o);
                if(pair != null && pair.equals(option.getId())){
                    return o;
                }
            }
        }
        return null;
    }
    
    private Card getCardByOptionId(String id){
        for(Card c: leftCards){
            if(c.option.getId().equals(id)){
                return c;
            }
        }
        for(Card c: rightCards){
            if(c.option.getId().equals(id)){
                return c;
            }
        }
        
        return null;
    }
   
    
    private void selectCard(Card card){
        
        if(card == lastCard){
            lastCard = null;
            return;
        }
        
        if(lastCard==null){
            lastCard = card;
            //lastCard.button.setSelected(true);
            return;
        }
        
        if(card.left == lastCard.left){
            lastCard.reset();
            lastCard = card;
            //lastCard.button.setSelected(true);
            return;
        }
        
        if(lastCard.equals(card)){
            lastCard.complete();
            card.complete();
            lastCard.button.setSelected(true);
            //card.button.setSelected(true);
            lastCard = null;
            if(countPending() == 0){
                getActivity().setState(Activity.COMPLETED);
            }
            
        }else{
            
            
            lastCard.reset();
            card.reset();
            errors.add(new Card[]{ lastCard,card  });
            
            lastCard = null;
            ConnectActivityPanel.this.repaint();

        }
    }
    
    

    private int countPending(){
        int count = 0;
        for(Card c : leftCards){
            count += c.completed?0:1;
        }
        return count;
    } 
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        if(leftCards==null){
            return;
        }
        
        for(Card c  : leftCards){
            if(c.completed){
                g.setColor(Color.GREEN);
                int x1,y1,x2,y2;
                x1 = c.button.getX()+c.button.getWidth();
                y1 = c.button.getY()+c.button.getHeight()/2;
                x2 = c.pair.button.getX();
                y2 = c.pair.button.getY()+c.pair.button.getHeight()/2;
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y1+1, x2, y2+1);
                g.drawLine(x1, y1+2, x2, y2+2);
                g.drawLine(x1, y1+3, x2, y2+3);
                g.drawLine(x1, y1+4, x2, y2+4);
                g.drawLine(x1, y1+5, x2, y2+5);
            }
        }
        
        for(Card[] lastError: errors){
            Card c = lastError[0];
            Card p = lastError[1];
                g.setColor(Color.RED);
                int x1,y1,x2,y2;
                x1 = c.button.getX()+c.button.getWidth();
                y1 = c.button.getY()+c.button.getHeight()/2;
                x2 = p.button.getX();
                y2 = p.button.getY()+p.button.getHeight()/2;
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x1, y1+1, x2, y2+1);
                g.drawLine(x1, y1+2, x2, y2+2);
                g.drawLine(x1, y1+3, x2, y2+3);
                g.drawLine(x1, y1+4, x2, y2+4);
                g.drawLine(x1, y1+5, x2, y2+5);
         }
        
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    protected void build(Activity a) {
        
        if(a == null || a.getOptions() == null || a.getOptions().isEmpty()){
            return;
        }
        
        
        cardHeight = getThumbnailHeight(a);
        cardWidth  = getThumbnailWidth(a);

        
        
        createCards(a);
        Component[][] comps = new Component[leftCards.size()][3];
        
        leftCards.stream().forEach((left) -> {
            int pos;
            do{
                pos = (int)(Math.random()*comps.length);
            }while(comps[pos][0]!=null);
            comps[pos][0] = left.button;
        });

        rightCards.stream().forEach((right) -> {
            int pos;
            do{
                pos = (int)(Math.random()*comps.length);
            }while(comps[pos][2]!=null);
            comps[pos][2] = right.button;
        });
        
        
        setComponents(comps,
                new String[]{ Panel.NONE, Panel.FILL_GROW_CENTER, Panel.NONE },null);
    }

    @Override
    public void release() {
        //does not need any releas implementation
    }


    

    
    private class Card{

        ActivityOption option;
        ToggleButton button;
        Card pair;
        boolean completed;
        Image frontImage;
        boolean left = false;
        

        public void complete(){
            this.completed = true;
            this.button.setEnabled(false);
            ConnectActivityPanel.this.repaint();
        }
        
        
        public void setEnable(boolean enabled){
            if(!this.completed){
                this.button.setEnabled(enabled);
            }
        }
        
        public void reset(){
            this.completed = false;
            this.button.setEnabled(true);
            this.button.setSelected(false);
            this.button.setBorder(BorderFactory.createEmptyBorder());
            ConnectActivityPanel.this.repaint();
            ConnectActivityPanel.this.revalidate();
        }
        
        
        @Override
        public boolean equals(Object o2){
            return pair.option.getId().equals(((Card)o2).option.getId());
        }
        
    }
    
}
