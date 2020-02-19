package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class DictionaryFileReader extends Thread {
    private WordDAO wordDAO;
    private XmlPullParser parser;
    private Context context;

    /**
     * @param context The context to get dictionary file
     *                This constructor initializes the context, the DAO for word and the XML parser
     */
    public DictionaryFileReader(Context context) {
        this.context = context;
        wordDAO = LMemoDatabase.getInstance().wordDAO();
        try {
            parser = parse(context.getAssets().open(context.getString(R.string.dictionary_file_name)));
            //The following line is for test only
//            parser = parse(context.getAssets().open("dictionary_test.txt"));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        readFileToSQLite();
    }

    private void readFileToSQLite() {
        Log.i("START_READING", "Start reading file");
        deleteAllRemainsWords();
        try {
            addAllWordsToSQLite();
        } catch (IOException e) {
            Log.i("EXC", "IOException");
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            Log.i("EXC", "XmlPullException");
            e.printStackTrace();
        }
        Log.i("END_READING", "End reading file");
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Word parseWord(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        int wordID = Integer.parseInt(
                parser.getAttributeValue(null, "xml:id").substring(1));
        StringBuilder kana = new StringBuilder();
        StringBuilder kanji = new StringBuilder();
        Sense sense = new Sense();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("form")) {
                if (parser.getAttributeValue(null, "type").equals("k_ele")) {
                    kanji.append(readForm(parser));
                } else {
                    kana.append(readForm(parser));
                }
            } else if (name.equals("sense")) {
                sense = readSense(parser);
            } else {
                skip(parser);
            }
        }
        return new Word(wordID, kana.toString(), kanji.toString(), sense.meaning, sense.partOfSpeech);
    }

    private Sense readSense(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "sense");
        Sense result = new Sense();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String type = parser.getAttributeValue(null, "type");
            if (name.equals("note")) {
                if (type != null && type.equals("pos")) {
                    result.partOfSpeech += readNote(parser);
                } else {
                    skip(parser);
                }
            } else if (name.equals("cit")) {
                if (type.equals("trans")) {
                    result.meaning += readCit(parser);
                }
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private String readCit(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "cit");
        String result = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("quote")) {
                result = readQuote(parser);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private String readQuote(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "quote");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "quote");
        return result;
    }

    private String readNote(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "note");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "note");
        return result;
    }

    private String readForm(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "form");
        String result = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("orth")) {
                result = readOrth(parser);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private String readOrth(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "orth");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "orth");
        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return " / " + result;
    }

    private void deleteAllRemainsWords() {
        wordDAO.deleteAllWords();
        while (wordDAO.getAllWords().length != 0) {
        }
    }

    private void addAllWordsToSQLite() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "body");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                Word word = parseWord(parser);
                wordDAO.insertWord(word);
            } else {
                skip(parser);
            }
        }
    }

    private XmlPullParser parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();
            return parser;
        } finally {
//            in.close();
        }
    }

    private class Sense {
        String meaning = "";
        String partOfSpeech = "";
    }
}
