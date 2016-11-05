

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * This class reads a MIDI file, and stores a list of
 * note events as Note objects in an ArrayList
 * 
 * @author Evan X. Merz
 */
public class SimpleMidiReader
{
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	// the list of notes in the source MIDI file
	private ArrayList<Note> notes;

	// list of note on events - these must be coordinated with note off events
	private ArrayList<MidiEvent> midiNoteOnEvents;
	
	// the PulsesPerQuarterNote (or resolution) of the input piece
	public float ticksPerBeat = -1;
	// beats per measure - this can be changed using a META message that is not handled int his class
	private int beatsPerMeasure = 4;
	
	public boolean debug = false;
	
	// Constructors
	public SimpleMidiReader(String filename)
	{
		read(filename);
	}
	public SimpleMidiReader(String filename, boolean debug)
	{
		this.debug = debug;
		read(filename);
	}
	
	public ArrayList<Note> getNotes(){
		return notes;
	}
	public Note getNote(int i){
		return notes.get(i);
	}
	
	/**
	 * 
	 * @param Filename the MIDI file to read
	 */
	public void read(String filename)
	{
		// initialize our holders
		this.notes = new ArrayList<Note>();
		this.midiNoteOnEvents = new ArrayList<MidiEvent>();
		
		try
		{
			Sequence sequence = MidiSystem.getSequence(new File(filename));

			// store the time base of this sequence
			this.ticksPerBeat = (sequence.PPQ > 0.0) ? sequence.PPQ : sequence.getResolution();

			// loop through each track
			for (Track track : sequence.getTracks())
			{
				// loop through each MIDI event
				for (int i = 0; i < track.size(); i++)
				{
					// get the midi message
					MidiEvent newEvent = track.get(i);
					MidiMessage message = newEvent.getMessage();
					
					if (message instanceof ShortMessage)
					{
						ShortMessage sm = (ShortMessage) message;

						// if this is a NOTE_ON event
						if (sm.getCommand() == MidiUtil.NOTE_ON && sm.getData2() > 0 )
						{
							if( debug ) System.out.println("Note on: " + sm.getData1() + " " + sm.getData2() + " " + newEvent.getTick()); // for debugging only
							
							this.midiNoteOnEvents.add(newEvent);
						}
						else if( sm.getCommand() == MidiUtil.NOTE_OFF || (sm.getCommand() == MidiUtil.NOTE_ON && sm.getData2() == 0) )
						{
							// store the note off event
							MidiEvent noteOff = newEvent;
							
							if( debug ) System.out.println("Note off: " + sm.getData1() + " " + sm.getData2() + " " + newEvent.getTick()); // for debugging only
							
							// find the note on message with the same pitch
							MidiEvent noteOn = null;
							Iterator<MidiEvent> m = this.midiNoteOnEvents.iterator();
							NOTE_ON_SEARCH:
							while(m.hasNext())
							{
								MidiEvent tmpNoteOn = (MidiEvent)m.next();
								if( ((ShortMessage)tmpNoteOn.getMessage()).getData1() == ((ShortMessage)noteOff.getMessage()).getData1() )
								{
									// remove this note on message
									m.remove();
									// store noteOn
									noteOn = tmpNoteOn;
									break NOTE_ON_SEARCH;
								}
							}

							// store the information for the new note
							int newPitch = ((ShortMessage)noteOn.getMessage()).getData1();
							int newStartTick = (int)noteOn.getTick();
							int newEndTick = (int)noteOff.getTick();
							
							// create the new Note instance
							Note newNote = new Note((int) ticksPerBeat, beatsPerMeasure);
							newNote.setPitch(newPitch);
							newNote.setDuration(newStartTick, newEndTick);
							// add the new note
							notes.add(newNote);
								
							if( debug ) System.out.println("  note created. note.pitch = " + newNote.pitch); // for debugging only
						}
					}

				}
			}
	  }
	  catch(Exception ex)
	  {
		  System.out.println("Error while reading the MIDI file:" + ex.getMessage());
		  System.out.println(ex.getCause());
		  ex.printStackTrace();
		  System.out.println(ex.toString());
	  }
	}

}
