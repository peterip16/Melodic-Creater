


/**
 * Midi_Util.java
 * Code for saving Notes in an ArrayList
 * to a MIDI file.
 */

// these includes will allow us to write to a midi file
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;


/**
 * @author Evan X. Merz
 *
 */
public class MidiUtil
{
	// some constants that define NOTE_ON and NOTE_OFF events
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	public static final int TIME_SIGNATURE = 0x58;
	public static final int META = 0xFF;
	
	public static final String[] MIDI_PITCH_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"}; 
	
	
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
