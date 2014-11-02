package org.abrantix.tuner;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabrantes on 02/11/14.
 */
public class NoteFreqTable {

    private static List<Double> mBaseFreqs = new ArrayList<Double>();
    static {
        mBaseFreqs.add(32.7032); // C1
        mBaseFreqs.add(34.6478); // C#
        mBaseFreqs.add(36.7081); // D
        mBaseFreqs.add(38.8909); // D#
        mBaseFreqs.add(41.2034); // E
        mBaseFreqs.add(43.6535); // F
        mBaseFreqs.add(46.2493); // F#
        mBaseFreqs.add(48.9994); // G
        mBaseFreqs.add(51.9131); // G#
        mBaseFreqs.add(55.0000); // A
        mBaseFreqs.add(58.2705); // A#
        mBaseFreqs.add(61.7354); // B
    }

    private static List<String> mBaseNotes = new ArrayList<String>();
    static {
        mBaseNotes.add("C");
        mBaseNotes.add("C#");
        mBaseNotes.add("D");
        mBaseNotes.add("D#");
        mBaseNotes.add("E");
        mBaseNotes.add("F");
        mBaseNotes.add("F#");
        mBaseNotes.add("G");
        mBaseNotes.add("G#");
        mBaseNotes.add("A");
        mBaseNotes.add("A#");
        mBaseNotes.add("B");
    }

    private int mLowestOctave = 1;
    private int mHighestOctave = 2;
    private List<Note> mNotes;

    public NoteFreqTable(int lowestScale, int highestScale) {
        mLowestOctave = lowestScale;
        mHighestOctave = highestScale;
        mNotes = generateNoteList();
    }

    private List<Note> generateNoteList() {
        final List<Note> list = new ArrayList<Note>();
        for (int octave = mLowestOctave; octave <= mHighestOctave; octave++) {
            for (int noteIdx = 0; noteIdx < mBaseFreqs.size(); noteIdx++) {
                Note note = new Note();
                note.mName = mBaseNotes.get(noteIdx);
                note.mName = note.mName.charAt(0) + "" + octave + "" +
                        (note.mName.length() > 1 ? note.mName.charAt(1) : "");
                note.mFreq = mBaseFreqs.get(noteIdx) * Math.pow(2, octave - 1);
                list.add(note);
            }
        }
        return list;
    }

    @NonNull
    public Note getClosestNote(double freq) {
        Note closestNote = null;
        double minDist = Double.MAX_VALUE;
        for (Note note : mNotes) {
            Log.d("TAG", "note: " + note.mName);
            Log.d("TAG", "note: " + note.mFreq);
            final double dist = Math.abs(freq - note.mFreq);
            if (dist > minDist) {
                break;
            }
            minDist = dist;
            closestNote = note;
        }
        return closestNote;
    }

    public Note get(int i) {
        return mNotes.get(i);
    }


    public static class Note {
        String mName;
        double mFreq;
    }

}
