package com.example.bisrat.liguamharic;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//  displays the details of the a particular lesson in a separate screen

public class LessonActivity extends AppCompatActivity {

    private int lessonNum = 0;
    private ListView listView;
    private LessonAdapter lAdapter;
    ArrayList<Sentence> sentences = new ArrayList<Sentence>();
    ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);


        ActionBar actionBar = getSupportActionBar();
        ListView mDetailTV = (ListView) findViewById(R.id.sentences_list);

        // get data from prev activity when item of the
        // listview is clicked using intent
        Intent intent = getIntent();
        String mactionBarTitle =intent.getStringExtra("actionBarTitle");
        lessonNum = getIntent().getExtras().getInt("LESSON_NUMBER");


        // set action bar title
        actionBar.setTitle(mactionBarTitle);

        // parse the data and load it to arrayList
        parseXML();
        // make a list for each type of sentence
         ArrayList<String> englishOnly = new ArrayList<>();
         ArrayList<String> amharicOnly = new ArrayList<>();
         ArrayList<String> translitrationOnly = new ArrayList<>();

         // populate all english , amharic and transliteration lists
        for (Sentence sentence : sentences) {

            englishOnly.add(sentence.english);
            amharicOnly.add(sentence.amharic);
            translitrationOnly.add(sentence.transliteration);
        }

         // create a sentence and add it to the list

        for (int i =0; i < englishOnly.size(); i++){
            Sentence sent = new Sentence(englishOnly.get(i), amharicOnly.get(i)
                    , translitrationOnly.get(i));
            //bind all strings in an array
            sentenceList.add(sent);
        }


        // load the list to adapter
      lAdapter = new LessonAdapter(this,sentenceList);

       // sets the view to the adapter
        mDetailTV.setAdapter(lAdapter);

    }


    // Instantiates an Xml pull parser, receives the data from file
    // and pass it to parser, calls process parser for further data
    // processing
    private void parseXML() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            // get the data to parse in to inputStream
            InputStream is = getAssets().open("data.xml");
            // not using namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            parser.setInput(is, null);
            processParsing(parser);
        } catch (XmlPullParserException e) {

        } catch (IOException e) {

        }

    }

     // Takes the input data from parser object and steps through the lessons and
    //  sentences , populates sentence list and returns it
    private List<Sentence> processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        int eventType = parser.getEventType(); // returns the event type ex doc start,tag start etc
        Sentence currentSentence = null;
        boolean currentLesson = false;
        // keep parsing until we hit the end of file
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;
            // if the event is a start tag, grab the element tag check if its lesson
            // compares the lesson number and loads the sentences for that lesson
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName(); // returns tag name

                    if (eltName.equals("lesson")) {
                        currentLesson = parser.getAttributeValue(null,
                                "id").equals(Integer.toString(lessonNum));

                    } else if(currentLesson) {
                        if ("sentence".equals(eltName)) {
                            currentSentence = new Sentence();
                            sentences.add(currentSentence);
                        } else if (currentSentence != null) {
                            if ("english".equals(eltName)) {
                                currentSentence.english = parser.nextText();
                            } else if ("amharic".equals(eltName)) {
                                currentSentence.amharic = parser.nextText();
                            } else if ("transliteration".equals(eltName)) {
                                currentSentence.transliteration = parser.nextText();
                            }
                        }
                    }
                    break;
            }
            eventType = parser.next(); // advances parser

        }
        //Log.i("DEBUG", String.valueOf(sentences.size()));

         return sentences;
    }

}
