import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import weka.*;
//import MidiUtil.*;

public class chords4
{
	static int numberOfNotes;
	
	public static void main(String[] args)
	{
		ArrayList<Integer> pitches = new ArrayList<Integer>();
		ArrayList<Integer> duration = new ArrayList<Integer>(); 
		
		SimpleMidiReader smr = new SimpleMidiReader("input.mid");
		ArrayList<Note> notes = smr.getNotes();
		ArrayList<ArrayList<Integer>> currentChord = new ArrayList<ArrayList<Integer>>(); //List of notes, aka the track 
		numberOfNotes = notes.size();
		int currentStartTickSource = -1;
		//int[] currentLine = new int[5];
		ArrayList<Integer> currentLine = new ArrayList<Integer>(); //List of key hits, aka notes
		ArrayList<Integer> timeList = new ArrayList<Integer>(); //List of time gaps
		
		//for(int i: currentLine)
		//{
			//i = 0;
		//}
		
		//Setting variables for processing the currentChord
		int numberOfNotes = 0;
		int i = 1;
		int numberOfNotesProcessed = 0;
		boolean afterFirst = false;
		
		//Get the currentChord
		for (Note n : notes)
		{	
			numberOfNotesProcessed++; 
			if(n.startTickSource == currentStartTickSource)
			{
				int currentValue = n.pitch;
				//currentLine[i] = n.pitch;
				currentLine.add(n.pitch);
				i++;
			}
			else
			{
				timeList.add(n.startTickSource - currentStartTickSource);
				if(afterFirst)
				{
					ArrayList<Integer> currentLines = new ArrayList<Integer>();
					for(int t: currentLine)
					{
						currentLines.add(t);
					}
					currentChord.add(currentLines);
				}
				duration.add((n.startTickSource - currentStartTickSource)/10);
				
				//for(int e = 0; e < currentLine.length; e++)
				//{
					//currentLine[e] = 0;
				//}
				currentLine = new ArrayList<Integer>();
				//currentLine[0] = n.pitch;
				currentLine.add(n.pitch);
				i = 1;
				currentStartTickSource = n.startTickSource;
				afterFirst = true;
			}
		}
		duration.remove(0);
		duration.add(duration.get(0));
		ArrayList<Integer> currentLines = new ArrayList<Integer>();
		
		//add final notes
		for(int t: currentLine)
		{
			currentLines.add(t);
		}
		currentChord.add(currentLines);
		
		//Take data, getting scores from the original music and use as comparison for the final piece
		int tempLength = currentChord.size();
		int origMusicScore = getMusicScore(currentChord);
		
//		for(int n = 0; n < tempLength; n++)
//		{
//			totalDuration += timeList.get(n);
//			if(n > 0 && Math.abs(currentChord.get(n).get(0) - currentChord.get(n - 1).get(0)) < 4)
//			{
//				origMusicScore++;
//			}
//		}
		
		int origMusicDurationScore = getDurationScore(timeList);
		int origMusicAvgDuration = getAvgDuration(timeList);
		int origMusicSeparator = getSeparatorValue(timeList);
		
		for(i = 0; i < currentChord.size(); i++)
		{
			if(!isWithinOctave(currentChord.get(i), currentChord.get(0)))
			{
				currentChord.remove(i);
				timeList.remove(i);
				i -= 1;
			}
		}
		
		//create a population of new music with minimum 500 and maximum 1000 population
		
		ArrayList<ArrayList<ArrayList<Integer>>> newMusicPopulation = new ArrayList<ArrayList<ArrayList<Integer>>>(); //
		ArrayList<ArrayList<Integer>> newDurationPopulation = new ArrayList<ArrayList<Integer>>(); //The population of all the new musics duration
		int newPopulationSize = (int)(Math.random()*5000); //Generate minimum number to be added to the 
		newPopulationSize += 10000; //minimum population
		
		//loop just in case there's nothing left in the population
		while(newMusicPopulation.size() == 0)
		{
			for(i = 0; i < newPopulationSize; i++)
			{
				//Making new music 
			
				ArrayList<ArrayList<Integer>> newMusic = new ArrayList<ArrayList<Integer>>(); //Where the new music will be stored
				ArrayList<Integer> newMusicDuration = new ArrayList<Integer>(); //The gap of the new music
				
				newMusic.add(currentChord.get(0)); //Make sure this new music begins with 
				newMusicDuration.add(timeList.get(0)); //Make sure the new music has this time beginning
				
				numberOfNotes = 0;
				int currentDuration = 0; //Saves the current durations of the song
				
				boolean ended = false; //If the current note is a ending note, then this turns true
				
	//			for(int n = 0; n < 70 && !ended; n++) //Making sure this is at most 70 notes
	//			{
	//				if(n == 69) // Force the ending to the song
	//				{
	//					newMusic.add(currentChord.get(currentChord.size() - 1));
	//					newMusicDuration.add(currentDuration + (timeList.size() - 1));
	//				}
	//				else
	//				{
	//					//Generate two random number
	//					int ranNumOne = (int)(Math.random()*currentChord.size());
	//					int ranNumTwo = (int)(Math.random()*timeList.size());
	//					
	//					//Grab new note and put it into the new music list
	//					ArrayList<Integer> tempNote = currentChord.get(ranNumOne);
	//					newMusic.add(tempNote);
	//					
	//					//Increase the current time with the new duration
	//					currentDuration = (timeList.get(ranNumTwo)*2);
	//					newMusicDuration.add((currentDuration));
	//					
	//					//Check if the current note is the appropriate last note
	//					ended = checkDuplicate(tempNote, currentChord.get(currentChord.size() - 1));
	//					
	//				}
	//			}
				int maxNoteCount = (int)(Math.random()*50);
				maxNoteCount += 70;
				
				for(int n = 0; n < maxNoteCount && !ended; n++) //Making sure this is equivalent to maxNoteCount number of notes
				{
					if(n == maxNoteCount - 1) // Force the ending to the song
					{
						newMusic.add(currentChord.get(currentChord.size() - 1));
						newMusicDuration.add(currentDuration + (timeList.size() - 1));
					}
					else
					{
						//Generate two random number
						
						int ranNumOne = (int)(Math.random()*currentChord.size());
						int ranNumTwo = (int)(Math.random()*timeList.size());
						
						//Grab new note and put it into the new music list
						ArrayList<Integer> tempNote = currentChord.get(ranNumOne);
						newMusic.add(tempNote);
						
						//Increase the current time with the new duration
						currentDuration = (timeList.get(ranNumTwo)*2);
						newMusicDuration.add((currentDuration));
						
						//Check if the current note is the appropriate last note
						ended = checkDuplicate(tempNote, currentChord.get(currentChord.size() - 1));
						
	//					int ranNumOne = (int)(Math.random()*currentChord.size());
	//					int ranNumTwo = (int)(Math.random()*timeList.size());
	//					
	//					//Grab new note and put it into the new music list
	//					ArrayList<Integer> tempNote = currentChord.get(ranNumOne);
	//					while(!isWithinOctave(tempNote, currentChord.get(0)))
	//					{
	//						ranNumOne = (int)(Math.random()*currentChord.size());
	//						ranNumTwo = (int)(Math.random()*timeList.size());
	//						
	//						tempNote = currentChord.get(ranNumOne);
	//					}
	//					newMusic.add(tempNote);
						
						//Increase the current time with the new duration
						currentDuration = (timeList.get(ranNumTwo)*2);
						newMusicDuration.add((currentDuration));
						
						//Check if the current note is the appropriate last note
						ended = checkDuplicate(tempNote, currentChord.get(currentChord.size() - 1));
						
					}
				}
				
				newMusicPopulation.add(newMusic);
				newDurationPopulation.add(newMusicDuration);
			}
			
			//Attempt to sequence through the entire list of music that was generated
	//		for(i = 0; i < newMusicPopulation.size(); i++)
	//		{
	//			Object[] temp = addSequenceProperty(newMusicPopulation.get(i), newDurationPopulation.get(i), origMusicSeparator);
	//			newMusicPopulation.set(i, (ArrayList<ArrayList<Integer>>)temp[0]);
	//			newDurationPopulation.set(i, (ArrayList<Integer>)temp[1]);
	//		}
			
			//Begin using genetic algorithms to eliminate and breed(?) a new set of music population
			//for(int n = 0; n < 100; n++)
			//{
				ArrayList<ArrayList<ArrayList<Integer>>> tempMusicPopulation = new ArrayList<ArrayList<ArrayList<Integer>>>();
				ArrayList<ArrayList<Integer>> tempDurationPopulation = new ArrayList<ArrayList<Integer>>();
				
				for(i = 0; i < newMusicPopulation.size() && newMusicPopulation.size() > 1; i++)
				{
					int tempMusicScore = getMusicScore(newMusicPopulation.get(i)); //get the scores for the individual music for the current population
					int tempDurationScore = getDurationScore(newDurationPopulation.get(i)); //get the scores for the individual music duration for the current population
					int tempAvgMusicDuration = getAvgDuration(newDurationPopulation.get(i)); //get the average duration
					int tempTotalDuration = getTotalDuration(newDurationPopulation.get(i)); //get the total duration
					double octaveScore = getOctaveScore(newMusicPopulation.get(i), currentChord.get(0));
					//int happyScore = countHappyNotes(newMusicPopulation.get(i));
					
					if(tempMusicScore > origMusicScore + 9 && tempDurationScore > origMusicDurationScore + 1 && Math.abs(origMusicAvgDuration - tempAvgMusicDuration) < 400 && tempTotalDuration > 6000)
					{
						//if(isHappy(newMusicPopulation.get(i).size(), happyScore))
						//{
							tempMusicPopulation.add(newMusicPopulation.get(i));
							tempDurationPopulation.add(newDurationPopulation.get(i));
						//}
					}
				}
				
				newMusicPopulation = tempMusicPopulation;
				newDurationPopulation = tempDurationPopulation; 
				
				for(i = 0; i < newMusicPopulation.size() && newMusicPopulation.size() > 1; i++)
				{
					newMusicPopulation.set(i, changeSadNotes(newMusicPopulation.get(i)));
				}
				
				for(i = 0; i < newMusicPopulation.size() && newMusicPopulation.size() > 1; i++)
				{
					Object[] temp = addSequenceProperty(newMusicPopulation.get(i), newDurationPopulation.get(i), origMusicSeparator);
					newMusicPopulation.set(i, (ArrayList<ArrayList<Integer>>)temp[0]);
					newDurationPopulation.set(i, (ArrayList<Integer>)temp[1]);
				}
				
				for(i = 0; i < newMusicPopulation.size(); i++)
				{
					if(getTotalDuration(newDurationPopulation.get(i)) < 3000)
					{
						newMusicPopulation.remove(i);
						newDurationPopulation.remove(i);
						i -= 1;
					}
				}
		}
			
//			for(i = 0; i < newMusicPopulation.size() && newMusicPopulation.size() > 1; i++)
//			{
//				newMusicPopulation.set(i, changeSadNotes(newMusicPopulation.get(i));
//			}
			
			//System.out.println(getSeparatorValue(newDurationPopulation.get(0))); //testing the get separator value methods
		//}
		
		if(newMusicPopulation.size() != 1)
		{
			newMusicPopulation = combineMusic(newMusicPopulation);
			newDurationPopulation = combineDuration(newDurationPopulation);
		}
		
		if(newMusicPopulation.size() < 1) //in case no music remains
		{
			System.out.println("I am sorry. Music generation failed, please try again.");
		}
		else
		{
			System.out.println(newMusicPopulation.size()); //test to see how many music qualifies
			SimpleMidiWriter.saveToMidiFile("output.mid", newMusicPopulation.get(0), newDurationPopulation.get(0));
		}
	}
	
	//See if both notes are the same
	public static boolean checkDuplicate(ArrayList<Integer> temp, ArrayList<Integer> original)
	{
		boolean result = true;
		if(temp.size() != original.size())
		{
			result = false;
		}
		else
		{
			int length = temp.size();
			for(int i = 0; i < length && result; i++)
			{
				if(temp.get(i) != original.get(i))
				{
					result = false;
				}
			}
		}
		return result;
	}
	
	/*
	//Get a list of sequence from a temporary new music track
	public static ArrayList<ArrayList<Integer>> getSequence(ArrayList<ArrayList<Integer>> temp)
	{
		ArrayList<ArrayList<Integer>> sequence = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < temp.size(); i++)
		{
			
		}
		
		return sequence;
	}
	*/
	
	//Get a score for the music base on whether difference in notes exceed 3
	public static int getMusicScore(ArrayList<ArrayList<Integer>> temp)
	{
		int result = 0;
		for(int i = 0; i < temp.size() - 1; i++)
		{
			if(i != 0 && Math.abs(temp.get(i).get(0) - temp.get(i - 1).get(0)) < 3)
			{
				result++;
			}
		}
		
		if(result != 0)
		{
			result = temp.size()/result;
		}
		
		return result;
	}
	
	//Get a score for the duration base on the difference in durations
	public static int getDurationScore(ArrayList<Integer> temp)
	{
		int result = 0;
		for(int i = 0; i < temp.size() - 2; i++)
		{
			if(Math.abs(temp.get(i) - temp.get(i + 1)) < 65 && Math.abs(temp.get(i) - temp.get(i + 1)) > 45)
			{
				result++;
			}
		}
		
		if(result != 0)
		{
			result = temp.size()/result;
		}
		
		return result;
	}
	
	//Get a average duration for the song
	public static int getAvgDuration(ArrayList<Integer> temp)
	{
		int tempAvg = 0;
		
		for(int n = 0; n < temp.size(); n++)
		{
			tempAvg += temp.get(n);
		}
		tempAvg /= (int)temp.size();
		
		return tempAvg;
	}
	
	//Get the total duration of the current song
	public static int getTotalDuration(ArrayList<Integer> temp)
	{
		int tempTotal = 0;
		
		for(int n = 0; n < temp.size(); n++)
		{
			tempTotal += temp.get(n);
		}
		
		return tempTotal;
	}
	
	//Change the given music to have some sort of sequence
	public static Object[] addSequenceProperty(ArrayList<ArrayList<Integer>> tempMusic, ArrayList<Integer> tempDurationList, int sequenceSeparatorValue)
	{
		Object[] result = new Object[2];
		
		int numberOfNoteInCurrentSequence = 0;
		int durationAfterSeparator = 0;
		int maxNoteCount = tempMusic.size();
		
		ArrayList<ArrayList<Integer>> editedMusic = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> editedDurationList = new ArrayList<Integer>();
		
		boolean repeatOrReverse = false;
		
		for(int i = 0; i < maxNoteCount; i++)
		{
			if((tempDurationList.get(i) > sequenceSeparatorValue || durationAfterSeparator >= 1500) && durationAfterSeparator > 500) //Good(?) place to form sequence
			{
				if(!repeatOrReverse)
				{
					editedMusic = addRepeatMusicSequence(editedMusic, numberOfNoteInCurrentSequence);
					editedDurationList = addRepeatDurationSequence(editedDurationList, numberOfNoteInCurrentSequence);
				}
				else
				{
					editedMusic = addReverseMusicSequence(editedMusic, numberOfNoteInCurrentSequence);
					editedDurationList = addReverseDurationSequence(editedDurationList, numberOfNoteInCurrentSequence);
				}
				i += numberOfNoteInCurrentSequence;
				repeatOrReverse = isNextSequenceReverse();
				numberOfNoteInCurrentSequence = 0;
				durationAfterSeparator = 0;
			}
			else if(durationAfterSeparator < 500 && tempDurationList.get(i) > sequenceSeparatorValue)
			{
				editedMusic.add(tempMusic.get(i));
				int newDuration = sequenceSeparatorValue + 1;
				while(newDuration > sequenceSeparatorValue)
				{
					newDuration = (int)(Math.random()*tempDurationList.size());
				}
				tempDurationList.set(i, newDuration);
				editedDurationList.add(newDuration);
			}
			else
			{
				editedMusic.add(tempMusic.get(i));
				editedDurationList.add(tempDurationList.get(i));
				durationAfterSeparator += tempDurationList.get(i);
				numberOfNoteInCurrentSequence++;
			}
		}
		
		editedMusic.add(tempMusic.get(tempMusic.size() - 1));
		editedDurationList.add(tempDurationList.get(tempDurationList.size() - 1));
		
		result[0] = editedMusic;
		result[1] = editedDurationList;
		
		return result;
	}
	
	//Get the 80th percentile of the duration
	public static int getSeparatorValue(ArrayList<Integer> tempDurationList)
	{
		tempDurationList.sort(new Comparator<Integer>(){
			@Override
			public int compare(Integer durationOne, Integer durationTwo) 
			{
				return durationOne - durationTwo;
			}
		});
		
		int separatorValue = tempDurationList.get((int)(tempDurationList.size()*0.9));
		
		return separatorValue; 
	}
	
	//Add the previous sequence notes in repeat
	public static ArrayList<ArrayList<Integer>> addRepeatMusicSequence(ArrayList<ArrayList<Integer>> tempMusic, int numberOfNoteInSequence)
	{
		int currentPosition = tempMusic.size();
		for(int i = numberOfNoteInSequence; i > 0 && currentPosition >= numberOfNoteInSequence; i--)
		{
			tempMusic.add(tempMusic.get(currentPosition - i));
		}
		
		return tempMusic;
	}
	
	//Add the previous sequence duration in repeat
	public static ArrayList<Integer> addRepeatDurationSequence(ArrayList<Integer> durationList, int numberOfNoteInSequence)
	{
		int currentPosition = durationList.size();
		for(int i = numberOfNoteInSequence; i > 0 && currentPosition >= numberOfNoteInSequence; i--)
		{
			durationList.add(durationList.get(currentPosition - i));
		}
		
		return durationList;
	}
	
	//Add the previous sequence notes in reverse
	public static ArrayList<ArrayList<Integer>> addReverseMusicSequence(ArrayList<ArrayList<Integer>> tempMusic, int numberOfNoteInSequence)
	{
		int currentPosition = tempMusic.size();
		for(int i = 1; i <= numberOfNoteInSequence && currentPosition >= numberOfNoteInSequence; i++)
		{
			tempMusic.add(tempMusic.get(currentPosition - i));
		}
		
		return tempMusic;
	}
		
	//Add the previous sequence duration in reverse
	public static ArrayList<Integer> addReverseDurationSequence(ArrayList<Integer> durationList, int numberOfNoteInSequence)
	{
		int currentPosition = durationList.size();
		for(int i = 1; i <= numberOfNoteInSequence && currentPosition >= numberOfNoteInSequence; i++)
		{
			durationList.add(durationList.get(currentPosition - i));
		}
		
		return durationList;
	}
	
	//Generate the boolean value for whether next sequence should be repeated or done in reverse
	public static boolean isNextSequenceReverse()
	{
		int temp = (int)(Math.random()*100);
		if(temp > 50)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//A very debatable function counting notes without any high notes
	public static int countHappyNotes(ArrayList<ArrayList<Integer>> tempMusic)
	{
		boolean containLowNotes = false;
		int count = 0;
		
		for(int i = 0; i < tempMusic.size(); i++)
		{
			ArrayList<Integer> notes = tempMusic.get(i);
			for(int n = 0; n < notes.size(); n++)
			{
				if(notes.get(n) < 65)
				{
					containLowNotes = true;
				}
			}
			if(!containLowNotes)
			{
				count++;
			}
			containLowNotes = false;
		}
		
		return count;
	}
	
	//Say whether this song is happy
	public static boolean isHappy(int noteCount, int happyScore)
	{
		double ratio = happyScore/noteCount;
		if(ratio > 0.8)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//Attempt to change sad notes
	public static ArrayList<ArrayList<Integer>> changeSadNotes (ArrayList<ArrayList<Integer>> tempMusic)
	{
		for(int i = 0; i < tempMusic.size(); i++)
		{
			for(int n = 0; n < tempMusic.get(i).size(); n++)
			{
				if(tempMusic.get(i).get(n) < 55)
				{
					ArrayList<Integer> tempNote = tempMusic.get(i);
					int newValue = (int)(Math.random()*10);
					newValue += 55;
					tempNote.set(n, newValue);
					tempMusic.set(i, tempNote);
				}
			}
		}
		
		return tempMusic;
	}
	
	//Attempt to give score base on octave according to the average of the first note of the entire original song
	public static double getOctaveScore(ArrayList<ArrayList<Integer>> tempMusic, ArrayList<Integer> startingNote)
	{
		int result = 0;
		int total = 0;
		
		for(int i = 0; i < startingNote.size(); i++)
		{
			total += startingNote.get(i);
		}
		
		int startingPitch = total/startingNote.size();
		
		for(int i = 0; i < tempMusic.size(); i++)
		{
			
			int currentPitch = 0;
			
			for(int n = 0; n < tempMusic.get(i).size(); n++)
			{
//				if((int)(Math.abs(tempMusic.get(i).get(n) - startingPitch)) < 9)
//				{
//					result++;
//				}
				
				currentPitch += tempMusic.get(i).get(n);
			}
			
			currentPitch += tempMusic.get(i).size();
			
			if((int)(Math.abs(currentPitch - startingPitch)) < 9)
			{
				result++;
			}
			
		}
		
		result /= tempMusic.size();
		
		return result;
	}
	
	public static boolean isWithinOctave(ArrayList<Integer>currentNote, ArrayList<Integer> startingNote)
	{
		int result = 0;
		int total = 0;
		
		for(int i = 0; i < startingNote.size(); i++)
		{
			total += startingNote.get(i);
		}
		
		int startingPitch = total/startingNote.size();
		int currentPitch = 0;
		
		for(int i = 0; i < currentNote.size(); i++)
		{
			currentPitch += currentNote.get(i);
		}
		
		if((int)(Math.abs(currentPitch - startingPitch)) < 9)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static ArrayList<ArrayList<ArrayList<Integer>>> combineMusic(ArrayList<ArrayList<ArrayList<Integer>>> tempMusicPopulation)
	{
		ArrayList<ArrayList<ArrayList<Integer>>> finalMusicPiece = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<Integer>> finalMusic = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < tempMusicPopulation.size(); i++)
		{
			for(int n = 0; n < tempMusicPopulation.get(i).size(); n++)
			{
				finalMusic.add(tempMusicPopulation.get(i).get(n));
			}
		}
		
		finalMusicPiece.add(finalMusic);
		
		
		return finalMusicPiece;
	}
	
	public static ArrayList<ArrayList<Integer>> combineDuration(ArrayList<ArrayList<Integer>> tempDurationPopulation)
	{
		ArrayList<ArrayList<Integer>> finalDurationPiece = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> finalDuration = new ArrayList<Integer>();
		
		for(int i = 0; i < tempDurationPopulation.size(); i++)
		{
			for(int n = 0; n < tempDurationPopulation.get(i).size(); n++)
			{
				finalDuration.add(tempDurationPopulation.get(i).get(n));
			}
		}
		
		finalDurationPiece.add(finalDuration);
		
		return finalDurationPiece;
	}
}

