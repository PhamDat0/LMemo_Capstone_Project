package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;
import android.util.Xml;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class DictionaryFileReader {
    private WordDAO wordDAO;
    private XmlPullParser parser;
    private Context context;

    public DictionaryFileReader(Context context) {
        this.context = context;
        wordDAO = LMemoDatabase.getInstance().wordDAO();
        try {
            parser = parse(context.getAssets().open(context.getString(R.string.dictionary_file_name)));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static Word parseWord(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "entry");
        int wordID = Integer.parseInt(
                parser.getAttributeValue(null, "xml:id").substring(1));
        String kana = "";
        String kanji = "";
        String meaning = "";
        String partOfSpeech = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("form")) {
                if (parser.getAttributeValue(null, "type").equals("k_ele")) {

                }
            } else {
                skip(parser);
            }
        }
        return new Word(wordID, kana, kanji, meaning, partOfSpeech);
    }

    public void readFileToSQLite(Context context) {
        deleteAllRemainsWords();
        try {
            addAllWordsToSQLite();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllRemainsWords() {
        wordDAO.deleteAllWords();
        while (wordDAO.getAllWords().length != 0) {
        }
    }

    private void addAllWordsToSQLite() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "feed");
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
            in.close();
        }
    }
}
