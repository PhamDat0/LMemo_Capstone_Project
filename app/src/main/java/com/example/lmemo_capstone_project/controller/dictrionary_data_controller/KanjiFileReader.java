package com.example.lmemo_capstone_project.controller.dictrionary_data_controller;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.lmemo_capstone_project.R;
import com.example.lmemo_capstone_project.controller.SharedPreferencesController;
import com.example.lmemo_capstone_project.controller.database_controller.LMemoDatabase;
import com.example.lmemo_capstone_project.controller.database_controller.room_dao.KanjiDAO;
import com.example.lmemo_capstone_project.model.room_db_entity.Kanji;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class KanjiFileReader extends Thread {
    private KanjiDAO kanjiDAO;
    private XmlPullParser parser;
    private Context context;

    /**
     * @param context The context to get dictionary file
     *                This constructor initializes the context, the DAO for Kanji and the XML parser
     */
    public KanjiFileReader(Context context) {
        this.context = context;
        kanjiDAO = LMemoDatabase.getInstance(context).kanjiDAO();
        try {
            parser = parse(new BufferedInputStream(context.getAssets().open(context.getString(R.string.kanji_file_name))));
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

    /**
     * This method first deletes every record in the SQLite to avoid conflict.
     * Then it will add every kanji in the kanji file to SQLite.
     * この関数は最初にSQLiteから残っている漢字を削除します。それから、辞書のファイルの中の漢字を読んでSQLiteに書きます。
     */
    @Override
    public void run() {
        if (!SharedPreferencesController.hasKanjiData(context)) {
            Log.i("START_READING", "Start reading file");
            deleteAllRemainingKanji();
            try {
                addKanjiToSQLite();
            } catch (IOException e) {
                Log.i("EXC", "IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.i("EXC", "XmlPullException");
                e.printStackTrace();
            }
            SharedPreferencesController.setKanjiDataState(context, true);
            Log.i("END_READING", "End reading file");
        }
    }

    /**
     * @param parser The XMLPullParser using to parse the XML file.
     * @return The kanji parsed from the kanji file
     * @throws IOException            This exception is thrown if reading file issue occurs
     * @throws XmlPullParserException This exception is thrown if there are problems with parsing xml
     */
    private Kanji parseKanji(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "character");
        String kanji = "";
        Pronunciation pronunciation = new Pronunciation();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
//            Log.i("TAG_NAME",name);
            if (name.equals("literal")) {
                kanji = readLiteral(parser).substring(3);
//                Log.i("KANJI_ID",kanji+ " " + (kanjiDAO.getKanji("唖").length>0?kanjiDAO.getKanji("唖")[0].getKanji()+"":"n"));
            } else if (name.equals("reading_meaning")) {
                pronunciation = readPronunciation(parser);
            } else {
                skip(parser);
            }
        }
        Kanji k = new Kanji();
        k.setKanji(kanji);
//        Log.i("Done_1",kanji);
        k.setOnyomi(pronunciation.onyomi.length() >= 3 ? pronunciation.onyomi.substring(3) : pronunciation.onyomi);
        k.setKunyomi(pronunciation.kunyomi.length() >= 3 ? pronunciation.kunyomi.substring(3) : pronunciation.kunyomi);
        return k;
    }

    private Pronunciation readPronunciation(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "reading_meaning");
        Pronunciation result = new Pronunciation();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("rmgroup")) {
                result = readRMGroup(parser);
            } else {
                skip(parser);
            }
        }

        return result;
    }

    private Pronunciation readRMGroup(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "rmgroup");
        Pronunciation result = new Pronunciation();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String r_type = parser.getAttributeValue(null, "r_type");
            if (name.equals("reading")) {
                if (r_type.equals("ja_on")) {
                    result.onyomi += readReading(parser);
                } else if (r_type.equals("ja_kun")) {
                    result.kunyomi += readReading(parser);
                } else {
                    skip(parser);
                }
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private String readReading(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "reading");
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "reading");
        return result;
    }

    private String readLiteral(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "literal");
        String result = readText(parser);
//        Log.i("KANJI_NULL",result);
        parser.require(XmlPullParser.END_TAG, null, "literal");
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
     * 　この関数はSQLiteから残っている漢字を削除します。
     */
    private void deleteAllRemainingKanji() {
        kanjiDAO.deleteAllKanji();
        while (kanjiDAO.getKanji("%").length != 0) {
        }
    }

    /**
     * @throws IOException            This exception is thrown if reading file issue occurs
     * @throws XmlPullParserException This exception is thrown if there are problems with parsing xml
     *                                file.
     *                                この関数は辞書のファイルの中の漢字を読んでSQLiteに書きます。
     */
    private void addKanjiToSQLite() throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "all");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the character tag
            // 「character」というタグは1つの漢字ですから、このタグを探して解析します。
            if (name.equals("character")) {
                Kanji kanji = parseKanji(parser);
//                Log.i("KANJI_ID",kanji.getKanji()+ " " + (kanjiDAO.getKanji("唖").length>0?kanjiDAO.getKanji("唖")[0].getKanji()+"":"n"));
                kanjiDAO.insertKanji(kanji);
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

    private class Pronunciation {
        public String onyomi = "";
        public String kunyomi = "";
    }
}
