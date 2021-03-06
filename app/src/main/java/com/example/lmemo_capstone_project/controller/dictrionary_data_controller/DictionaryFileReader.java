package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.WordDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Word;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DictionaryFileReader extends Thread {
    private WordDAO wordDAO;
    private XmlPullParser parser;
    private Context context;

    /**
     * @param context The context to get dictionary file
     *                辞書ファイルを取るためのContextオブジェクト
     * This constructor initializes the context, the DAO for word and the XML parser
     * この構築子はContextとWordDAOとXML分析エンジンのオブジェクトを初期化子します。
     */
    public DictionaryFileReader(Context context) {
        this.context = context;
        wordDAO = LMemoDatabase.getInstance(context).wordDAO();
        try {
            parser = parse(new BufferedInputStream(context.getAssets().open(context.getString(R.string.dictionary_file_name))));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method first deletes every record in the SQLite to avoid conflict.
     * Then it will add every word in the dictionary file to SQLite.
     * この関数は最初にSQLiteから残っている言葉を削除します。それから、辞書のファイルの中の言葉を読んで
     * SQLiteに書きます。
     */
    @Override
    public void run() {
        if (!SharedPreferencesController.hasDictionaryData(context)) {
            Log.i("START_READING", "Start reading file");
            deleteAllRemainingWords();
            try {
                addAllWordsToSQLite();
            } catch (IOException e) {
                Log.i("EXC", "IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.i("EXC", "XmlPullException");
                e.printStackTrace();
            }
            SharedPreferencesController.setDictionaryDataState(context, true);
            Log.i("END_READING", "End reading file");
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

    /**
     * @param parser The XMLPullParser using to parse the XML file.
     *               これはXMLファイルを分析のためのXMLPullParserオブジェクト。
     * @return The word parsed from the dictionary file
     *          辞書ファイルから分析した言葉。
     * @throws IOException  This exception is thrown if reading file issue occurs
     *                      ファイルを読む問題が起これば、この例外は投げられます。
     * @throws XmlPullParserException   This exception is thrown if there are problems with parsing xml
     * file.
     *                                  XMLファイルを分析する問題が起これば、この例外は投げられます。
     * この関数は辞書のファイルの中の言葉を読んでSQLiteに書きます。
     */
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
                sense = readSense(parser, sense);
            } else {
                skip(parser);
            }
        }
        return new Word(wordID, kana.length() >= 3 ? kana.toString().substring(3) : kana.toString(),
                kanji.length() >= 3 ? kanji.toString().substring(3) : kanji.toString(),
                sense.meaning.length() >= 3 ? sense.meaning.substring(3) : sense.meaning,
                sense.partOfSpeech.length() >= 3 ? sense.partOfSpeech.substring(3) : sense.partOfSpeech);
    }

    private Sense readSense(XmlPullParser parser, Sense result) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "sense");
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
        result.meaning += "\n";
        result.meaning = result.meaning.replace("\n / ", "\n");
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

    /**
     *　この関数はSQLiteから残っている言葉を削除します。
     */
    private void deleteAllRemainingWords() {
        wordDAO.deleteAllWords();
        while (wordDAO.getAllWords().length != 0) {
        }
    }

    /**
     * @throws IOException  This exception is thrown if reading file issue occurs
     *                      ファイルを読む問題が起これば、この例外は投げられます。
     * @throws XmlPullParserException   This exception is thrown if there are problems with parsing xml
     * file.
     *                                  XMLファイルを分析する問題が起これば、この例外は投げられます。
     * この関数は辞書のファイルの中の言葉を読んでSQLiteに書きます。
     */
    private void addAllWordsToSQLite() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "body");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            // 「entry」というタグは1つの言葉ですから、このタグを探して解析します。
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
