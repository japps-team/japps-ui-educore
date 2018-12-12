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
package japps.ui.educore.object;

import japps.ui.educore.component.ChooseActivityPanel;
import japps.ui.educore.component.ConnectActivityPanel;
import japps.ui.educore.component.DnDActivityPanel;
import japps.ui.educore.component.InteractiveImageActivityPanel;
import japps.ui.educore.component.MemoryActivityPanel;
import japps.ui.educore.component.ReadActivityPanel;
import java.awt.Font;
import java.nio.file.Path;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Const {

    public static final String ACTIVITY_PANEL_CLASS = "activityClass";
    public static final String MEDIA_TYPE   = "mediaType";
    public static final String MEDIA = "media";
    public static final String TEXT = "text";
    public static final String TITLE = "title";
    public static final String SHOW_TEXT = "showText";
    public static final String SHOW_MEDIA = "showMedia";
    public static final String SPEECH_TEXT = "speechText";
    public static final String FONT = "font";
    public static final String TIME_TO_SHOW = "time2show";
    public static final String THUMBNAIL = "thumbnail";
    public static final String ROWS      = "rows";
    public static final String COLUMNS   = "columns";
    public static final String ROW      = "row";
    public static final String COLUMN   = "column";
    public static final String THUMBNAIL_HEIGHT = "thumbnailHeight";
    public static final String THUMBNAIL_WIDTH = "thumbnailWidth";
    public static final String THUMBNAIL_BACK  = "thumbnailBack";
    public static final String SUCCESS_IMAGE = "successImage";
    public static final String PAIR_OPTION_ID = "pairOption";
    public static final String FINAL_TEXT = "finalText";
    public static final String WAIT_UNTIL_COMPLETE = "waitUntilComplete";
    public static final String IMAGE = "image";
    public static final String WIDTH = "image";
    public static final String HEIGHT = "image";
    
    public static class LEARNING{
        public static String    getTitle(ActivityOption activity){     return activity.get(    TITLE); }
        public static boolean   isSpeechText(ActivityOption option){   return option.getBool(  SPEECH_TEXT);}
        public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        public static String    getFinalText(ActivityOption option){   return option.get(      FINAL_TEXT);}
        public static Path      getThumbnail(ActivityOption option){   return option.getPath(  THUMBNAIL);}
        public static boolean   isWaitUntilComplete(ActivityOption o){ return o.getBool(WAIT_UNTIL_COMPLETE);}
        
        public static void set(Learning learning, String title, String text, String finalText, Path thumbnail,boolean speechText , boolean waitUntilComplete){
            learning.set(TITLE, title);
            learning.set(SPEECH_TEXT, speechText);
            learning.set(TEXT, text);
            learning.set(FINAL_TEXT, finalText);
            learning.set(THUMBNAIL, thumbnail);
            learning.set(WAIT_UNTIL_COMPLETE, waitUntilComplete);
        }
        
    }
    
    public static class COMMON{
        public static String    getTitle(ActivityOption activity){     return activity.get(    TITLE); }
        public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        public static boolean   isSpeechText(ActivityOption option){   return option.getBool(SPEECH_TEXT);}
        public static Path      getSuccessImage(ActivityOption option){return option.getPath(  SUCCESS_IMAGE);}
        
        public static void activity(Activity activity, String title, String textOnComplete, Path successImage, boolean speechText){
            activity.set(TITLE, title);
            activity.set(TEXT, textOnComplete);
            activity.set(SUCCESS_IMAGE, successImage);
            activity.set(SPEECH_TEXT,speechText);
        }
    }
    
    public static class DND extends COMMON{
        
        public static Path      getThumbnail(ActivityOption option){   return option.getPath(  THUMBNAIL);}
        public static int       getRow(ActivityOption option){   return option.getInt(ROW);    }
        public static int       getColumn(ActivityOption option){   return option.getInt(COLUMN);    }
        public static int       getRows(ActivityOption option){   return option.getInt(ROWS);    }
        public static int       getColumns(ActivityOption option){   return option.getInt(COLUMNS);    }
        public static int       getThumbnailHeight(Activity activity){ return activity.getInt( THUMBNAIL_HEIGHT); } 
        public static int       getThumbnailWidth(Activity activity){  return activity.getInt( THUMBNAIL_WIDTH); } 
        
        
        /**
         * An option for this activity
         * @param o
         * @param title
         * @param text
         * @param speechText 
         * @param row 
         * @param column 
         */
        public static void option(ActivityOption o, String title, String text, boolean speechText, int row, int column, Path thumbnail){
            o.set(TITLE, title);
            o.set(TEXT, text);
            o.set(SPEECH_TEXT, speechText);
            o.set(ROW, row);
            o.set(COLUMN, column);
            o.set(THUMBNAIL, thumbnail);
        }
        
        public static void activity(Activity a, int rows, int columns, int thumbnailWidth, int thumbnailHeight){
            a.set(ACTIVITY_PANEL_CLASS, DnDActivityPanel.class.getName());
            a.set(ROWS, rows);
            a.set(COLUMNS, columns);
            a.set(THUMBNAIL_WIDTH,thumbnailWidth);
            a.set(THUMBNAIL_HEIGHT, thumbnailHeight);
        }
    }
    
    public static class INTERACTIVE_IMAGE extends COMMON{
        public static Path      getImage(ActivityOption option){   return option.getPath(  IMAGE); }
        public static int       getRow(ActivityOption option){   return option.getInt(ROW);    }
        public static int       getColumn(ActivityOption option){   return option.getInt(COLUMN);    }
        public static Path      getMedia(ActivityOption option){  return option.getPath(MEDIA); }
        public static int       getMediaType(ActivityOption option){ return option.getInt(MEDIA_TYPE); }
        
        /**
         * An option for this activity
         * @param o
         * @param text
         * @param media
         * @param mediaType 
         * @param speechText 
         * @param row 
         * @param column 
         */
        public static void option(ActivityOption o, String title, String text, Path media, int mediaType, boolean speechText, int row, int column){
            o.set(TITLE, title);
            o.set(TEXT, text);
            o.set(MEDIA, media);
            o.set(MEDIA_TYPE,mediaType);
            o.set(SPEECH_TEXT, speechText);
            o.set(ROW, row);
            o.set(COLUMN, column);
        }
        /**
         * Sets values for an activity
         * @param a
         * @param image
         */
        public static void activity(Activity a, Path image){
            a.set(ACTIVITY_PANEL_CLASS, InteractiveImageActivityPanel.class.getName());
            a.set(IMAGE, image);
        }
    }
    
    public static class CONNECT extends COMMON{
        //public static String    getTitle(ActivityOption activity){     return activity.get(    TITLE); }
        public static int       getThumbnailHeight(Activity activity){ return activity.getInt( THUMBNAIL_HEIGHT); } 
        public static int       getThumbnailWidth(Activity activity){  return activity.getInt( THUMBNAIL_WIDTH); } 
        public static Path      getThumbnail(ActivityOption option){   return option.getPath(  THUMBNAIL);}
        //public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        //public static boolean   isSpeechText(ActivityOption option){   return option.getBool(SPEECH_TEXT);}
        //public static Path      getSuccessImage(ActivityOption option){return option.getPath(  SUCCESS_IMAGE);}
        public static String    getPairOptionId(ActivityOption option){return option.get(PAIR_OPTION_ID); }
        
        /**
         * Set values to an activity option
         * @param option object to set the values
         * @param thumbnail Const.THUMBNAIL
         * @param text Const.TEXT
         * @param speechText Const.SPEECH_TEXT
         * @param pairOptionId
         */
        public static void option(ActivityOption option, Path thumbnail, String text, boolean speechText, String pairOptionId){
            option.set(THUMBNAIL, thumbnail);
            option.set(TEXT, text );
            option.set(SPEECH_TEXT, speechText );
            option.set(PAIR_OPTION_ID, pairOptionId);
        }
        
        /**
         * Set values to an activity
         * @param activity
         * @param thumbnailWidth
         * @param thumbnailHeight 
         */
        public static void activity(Activity activity, int thumbnailWidth, int thumbnailHeight){
            activity.set(ACTIVITY_PANEL_CLASS, ConnectActivityPanel.class.getName());
            activity.set(THUMBNAIL_WIDTH, thumbnailWidth);
            activity.set(THUMBNAIL_HEIGHT, thumbnailHeight);
        }

    }
    
    public static class MEMORY{
        public static String    getTitle(ActivityOption activity){     return activity.get(    TITLE); }
        public static int       getRows(Activity activity){            return activity.getInt( ROWS); }
        public static int       getColumns(Activity activity){         return activity.getInt( COLUMNS); }
        public static int       getThumbnailHeight(Activity activity){ return activity.getInt( THUMBNAIL_HEIGHT); } 
        public static int       getThumbnailWidth(Activity activity){  return activity.getInt( THUMBNAIL_WIDTH); } 
        public static Path      getThumbnail(ActivityOption option){   return option.getPath(  THUMBNAIL);}
        public static Path      getThumbnailBack(ActivityOption option){   return option.getPath(  THUMBNAIL_BACK);}
        public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        public static boolean   isSpeechText(ActivityOption option){   return option.getBool(SPEECH_TEXT);}
        public static Path      getSuccessImage(ActivityOption option){   return option.getPath(  SUCCESS_IMAGE);}
        
        /**
         * Set values to an activity option
         * @param option object to set the values
         * @param thumbnail Const.THUMBNAIL
         * @param text Const.TEXT
         * @param speechText Const.SPEECH_TEXT
         */
        public static void option(ActivityOption option, Path thumbnail, String text, boolean speechText){
            option.set(THUMBNAIL, thumbnail);
            option.set(TEXT, text );
            option.set(SPEECH_TEXT, speechText );
        }
        
        /**
         * Set values to an activity
         * @param activity
         * @param rows
         * @param columns
         * @param thumbnailWidth
         * @param thumbnailHeight 
         * @param thumbnailBack  Image for the back of all cards
         */
        public static void activity(Activity activity, int rows, int columns, int thumbnailWidth, int thumbnailHeight, Path thumbnailBack){
            activity.set(ACTIVITY_PANEL_CLASS, MemoryActivityPanel.class.getName());
            activity.set(ROWS, rows);
            activity.set(COLUMNS, columns);
            activity.set(THUMBNAIL_WIDTH, thumbnailWidth);
            activity.set(THUMBNAIL_HEIGHT, thumbnailHeight);
            activity.set(THUMBNAIL_BACK, thumbnailBack);
        }
        
    }

    
    public static class READ{
        public static String    getTitle(Activity activity){     return activity.get(    TITLE); }
        public static Path      getMedia(ActivityOption option){       return option.getPath(  MEDIA); }
        public static int       getMediaType(ActivityOption option){   return option.getInt(   MEDIA_TYPE);}
        public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        public static boolean   isSpeechText(ActivityOption option){   return option.getBool(SPEECH_TEXT);}
        public static int       getTimeToShow(ActivityOption option){  return option.getInt(TIME_TO_SHOW);}
        public static Font      getFont(ActivityOption option){        return option.getFont(FONT);}
        public static boolean   isShowText(ActivityOption option){     return option.getBool(SHOW_TEXT);}
        public static boolean   isShowMedia(ActivityOption option){    return option.getBool(SHOW_MEDIA);}
        
        public static void option(ActivityOption option, Path media, int mediaType, String text, int timeToshow, Font font,boolean speechText, boolean showText, boolean showMedia){
            option.set(MEDIA, media);
            option.set(MEDIA_TYPE, mediaType);
            option.set(TEXT, text );
            option.set(SPEECH_TEXT, speechText );
            option.set(TIME_TO_SHOW, timeToshow );
            option.set(FONT, font );
            option.set(SHOW_MEDIA, showMedia);
            option.set(SHOW_TEXT, showText);
        }
        
        public static void activity(Activity activity){
            activity.set(ACTIVITY_PANEL_CLASS, ReadActivityPanel.class.getName());
        }
    }
    
    public static class CHOOSE{
        public static String    getTitle(ActivityOption activity){     return activity.get(    TITLE); }
        public static int       getRows(Activity activity){            return activity.getInt( ROWS); }
        public static int       getColumns(Activity activity){         return activity.getInt( COLUMNS); }
        public static int       getThumbnailHeight(Activity activity){ return activity.getInt( THUMBNAIL_HEIGHT); } 
        public static int       getThumbnailWidth(Activity activity){  return activity.getInt( THUMBNAIL_WIDTH); } 
        public static Path      getThumbnail(ActivityOption option){   return option.getPath(  THUMBNAIL);}
        public static Path      getMedia(ActivityOption option){       return option.getPath(  MEDIA); }
        public static int       getMediaType(ActivityOption option){   return option.getInt(   MEDIA_TYPE);}
        public static String    getText(ActivityOption option){        return option.get(      TEXT);}
        public static boolean   isSpeechText(ActivityOption option){   return option.getBool(SPEECH_TEXT);}
        
        /**
         * Set values to an activity option
         * @param option object to set the values
         * @param title  Const.TITLE
         * @param thumbnail Const.THUMBNAIL
         * @param media Const.MEDIA
         * @param mediaType Const.MEDIA_TYPE
         * @param text Const.TEXT
         * @param speechText Const.SPEECH_TEXT
         */
        public static void option(ActivityOption option,String title, Path thumbnail, Path media, int mediaType, String text, boolean speechText){
            option.set(TITLE, title);
            option.set(THUMBNAIL, thumbnail);
            option.set(MEDIA, media);
            option.set(MEDIA_TYPE, mediaType);
            option.set(TEXT, text );
            option.set(SPEECH_TEXT, speechText );
        }
        
        public static void activity(Activity activity, int rows, int columns, int thumbnailWidth, int thumbnailHeight ){
            activity.set(ACTIVITY_PANEL_CLASS, ChooseActivityPanel.class.getName());
            activity.set(ROWS, rows);
            activity.set(COLUMNS, columns);
            activity.set(THUMBNAIL_WIDTH, thumbnailWidth);
            activity.set(THUMBNAIL_HEIGHT, thumbnailHeight);
        }
        
    }
    
}
