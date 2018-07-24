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

/**
 * Class holding parts-of-speech strings and their codes
 * @author Matt
 */
public class POSStrings {
    
    public static final String COORD_CONJ = "CC";
    public static final String CARDINAL_NUM = "CD";
    public static final String DETERMINER = "DT";
    public static final String EXIST_THERE = "EX";
    public static final String FOREIGN_WORD = "FW";
    public static final String PREP_SUB_CONJ = "IN";
    public static final String ADJECTIVE = "JJ";
    public static final String ADJECTIVE_C = "JJR";
    public static final String ADJECTIVE_S = "JJS";
    public static final String LIST_ITEM_MARKER = "LS";
    public static final String MODAL = "MD";
    public static final String NOUN = "NN";
    public static final String NOUN_P = "NNS";
    public static final String NOUN_PROPER = "NNP";
    public static final String NOUN_PROPER_P = "NNPS";
    public static final String PREDETERMINER = "PDT";
    public static final String POSSESSIVE_ENDING = "POS";
    public static final String PERSON_PRONOUN = "PRP";
    public static final String POSSESSIVE_PRONOUN = "PRP$";
    public static final String ADVERB = "RB";
    public static final String ADVERB_C = "RBR";
    public static final String ADVERB_S = "RBS";
    public static final String PARTICLE = "RP";
    public static final String SYMBOL = "SYM";
    public static final String TO = "TO";
    public static final String INTERJECTION = "UH";
    public static final String VERB = "VB";
    public static final String VERB_PAST = "VBD";
    public static final String VERB_GERUND = "VBG";
    public static final String VERB_PAST_PARTICIPLE = "VBN";
    public static final String VERB_NONTHIRD_SINGULAR_PRESENT = "VBP";
    public static final String VERB_THIRD_SINGULAR_PRESENT = "VBZ";
    public static final String WHDETERMINER = "WDT";
    public static final String WHPRONOUN = "WP";
    public static final String WHPRONOUN_POSSESSIVE = "WP$";
    public static final String WHADVERB = "WRB";
    
    /**
     * Returns understandable representation of a pos code.
     * @param tag
     * @return 
     */
    public static String get(String tag) {
        switch(tag) {
            case POSStrings.COORD_CONJ:
                return "Coordinating conjunction";
            case POSStrings.CARDINAL_NUM:
                return "Cardinal number";
            case POSStrings.DETERMINER:
                return "Determiner";
            case POSStrings.EXIST_THERE:
                return "EX";
            case POSStrings.FOREIGN_WORD:
                return "Foreign word";
            case POSStrings.PREP_SUB_CONJ:
                return "Preposition or subordinating conjunction";
            case POSStrings.ADJECTIVE:
                return "Adjective";
            case POSStrings.ADJECTIVE_C:
                return "Adjective, comparative";
            case POSStrings.ADJECTIVE_S:
                return "Adjective, superlative";
            case POSStrings.LIST_ITEM_MARKER:
                return "List item marker";
            case POSStrings.MODAL:
                return "Modal";
            case POSStrings.NOUN:
                return "Noun, singular or mass";
            case POSStrings.NOUN_P:
                return "Noun, plural";
            case POSStrings.NOUN_PROPER:
                return "Proper noun, singular";
            case POSStrings.NOUN_PROPER_P:
                return "Proper noun, plural";
            case POSStrings.PREDETERMINER:
                return "Preterminer";
            case POSStrings.POSSESSIVE_ENDING:
                return "Possessive ending";
            case POSStrings.PERSON_PRONOUN:
                return "Person pronoun";
            case POSStrings.POSSESSIVE_PRONOUN:
                return "Possessive pronoun";
            case POSStrings.ADVERB:
                return "Adverb";
            case POSStrings.ADVERB_C:
                return "Adverb, comparative";
            case POSStrings.ADVERB_S:
                return "Adverb, superlative";
            case POSStrings.PARTICLE:
                return "Particle";
            case POSStrings.SYMBOL:
                return "Symbol";
            case POSStrings.TO:
                return "to";
            case POSStrings.INTERJECTION:
                return "Interjection";
            case POSStrings.VERB:
                return "Verb, base form";
            case POSStrings.VERB_PAST:
                return "Verb, past tense";
            case POSStrings.VERB_GERUND:
                return "Verb, gerund or present participle";
            case POSStrings.VERB_PAST_PARTICIPLE:
                return "Verb, past participle";
            case POSStrings.VERB_NONTHIRD_SINGULAR_PRESENT:
                return "Verb, non3rd person singular present";
            case POSStrings.VERB_THIRD_SINGULAR_PRESENT:
                return "Verb, 3rd person singular present";
            case POSStrings.WHDETERMINER:
                return "Whdeterminer";
            case POSStrings.WHPRONOUN:
                return "Whpronoun";
            case POSStrings.WHPRONOUN_POSSESSIVE:
                return "Possessive whpronoun";
            case POSStrings.WHADVERB:
                return "Whadverb";
            default:
                return "Unknown";
        }
    }
}
