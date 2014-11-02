package org.abrantix.tuner;

import android.support.annotation.NonNull;

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
        mBaseFreqs.add(34.6478); // D#
        mBaseFreqs.add(32.7032); // E
        mBaseFreqs.add(34.6478); // F
        mBaseFreqs.add(32.7032); // F#
        mBaseFreqs.add(34.6478); // G
        mBaseFreqs.add(32.7032); // G#
        mBaseFreqs.add(34.6478); // A
        mBaseFreqs.add(32.7032); // A#
        mBaseFreqs.add(34.6478); // B
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
            }
        }
        return list;
    }

    @NonNull
    public Note getClosestNote(double freq) {
        Note closestNote = null;
        double minDist = Double.MAX_VALUE;
        for (Note note : mNotes) {
            final double dist = Math.abs(freq - note.mFreq);
            if (dist > minDist) {
                break;
            }
            minDist = dist;
            closestNote = note;
        }
        return closestNote;
    }

    public static class Note {
        String mName;
        double mFreq;
    }

}
