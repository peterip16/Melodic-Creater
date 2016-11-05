


/**
 * SimpleMidiWriter.java
 * Code for saving a MIDI file using only
 * a list of Integer pitches and durations.
 */

// these includes will allow us to write to a midi file
import java.io.File;
import java.io.IOException;

import javax.sound.midi.Sequence;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.InvalidMidiDataException;

import java.util.ArrayList;

/**
 * This class contains the SaveToMidiFile method, which converts 
 * parallel ArrayLists of pitches and durations into a MIDI file.
 * 
 * Note that the durations should be indexes into a list of
 * possible durations. 0 through 7
 * 
 * pitches should be MIDI pitches from 0 to 127.
 * 
 * @author Evan X. Merz
 *
 */
public class SimpleMidiWriter
{
	// some constants that define NOTE_ON and NOTE_OFF events
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	// constants that define other events
	public static final int TIME_SIGNATURE = 0x58;
	public static final int META = 0xFF;
	
	// time resolution of midi file
	public static final int TIME_RESOLUTION = 120;
	// possible note durations
	public static final int[] DURATION = {30, 60, 90, 120, 180, 240, 360, 480};
	
	public static String saveToMidiFile(String Filename, ArrayList<ArrayList<Integer>> pitches, ArrayList<Integer> durations)
	{
		Sequence s = null;
		try
		{
			s = new Sequence(Sequence.PPQ, TIME_RESOLUTION); // create a new sequence
		}
		catch (InvalidMidiDataException e)
		{
			return e.getMessage();
		}
		for(int i = 0; i < pitches.get(0).size(); i++)
		{
			Track track = s.createTrack();
			
			// add the notes in the ArrayList  to the track
			int currentTick = 0;
			for( int n = 0; n < pitches.size(); n++ )
			{
				if(pitches.get(n).size() > i)
				{	
					int newPitch = pitches.get(n).get(i);
					
					boolean found = false;
					int newDuration = 0;
					
					while(!found)
					{
						int ranNum = (int)(Math.random()*durations.size());
						if((newDuration = durations.get(ranNum)) < 1000)
						{
							found = true;
						}
					}
					
					if( newPitch > -1 ) // -1 = rest
					{
						track.add(CreateNoteEvent(ShortMessage.NOTE_ON, newPitch, 96, currentTick));
						track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, newPitch, 0, currentTick + newDuration));
					}
						
					// move the CurrentTick
					currentTick += newDuration;
				}
				//System.out.println("n: " + n);
				//System.out.println("i: " + i);
				//System.out.println();
				
				/*
				int newPitch = pitches.get(n).get(i);
				int newDuration = durations.get(i);
				
				if( newPitch > -1 ) // -1 = rest
				{
					track.add(CreateNoteEvent(ShortMessage.NOTE_ON, newPitch, 96, currentTick));
					track.add(CreateNoteEvent(ShortMessage.NOTE_OFF, newPitch, 0, currentTick + newDuration));
				}
					
				// move the CurrentTick
				currentTick += newDuration;
				*/
			}
		}
			
	    try
	    {
	    	File OutputFile = new File(Filename);
	    	// 1 = multitrack midi file
	    	MidiSystem.write(s, 1, OutputFile);
	    }
	    catch (IOException e)
	    {
	    	return e.getMessage();
	    }
	    return "";
	}
	
	/**
	 * Create a midi event
	 * 
	 * @param nCommand NOTE_ON, NOTE_OFF, etc
	 * @param nKey 0-127
	 * @param nVelocity 0-127
	 * @param lTick the starting tick, as a count of PulsesPerQuarterNote
	 * @return a new MidiEvent
	 */
	private static MidiEvent CreateNoteEvent(int nCommand, int nKey, int nVelocity, long lTick)
	{
	  ShortMessage  message = new ShortMessage();
	  try
	  {
	    message.setMessage(nCommand, 0,  nKey, nVelocity);
	  }
	  catch (InvalidMidiDataException e)
	  {
	    e.printStackTrace();
	    System.exit(1);
	  }
	  MidiEvent event = new MidiEvent(message, lTick);
	  return event;
	}
}
