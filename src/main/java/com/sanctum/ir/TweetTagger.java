/*
 * Copyright (C) 2018 Matt
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
package com.sanctum.ir;

import java.io.File;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSTaggerME;

/**
 * Parts-of-speech Tagger for tagging the tweet contents.
 * @author Matt
 */
public class TweetTagger {
    
    // The model for the tagger to use
    public static String POS_MODEL_FILE = Configuration.get(Configuration.POS_LEARNING_MODEL);
    
    // Global tagger object for tagging tweets
    public static final POSTaggerME POS_TAGGER = new POSTaggerME(new POSModelLoader().load(new File(TweetTagger.POS_MODEL_FILE)));
    
}
