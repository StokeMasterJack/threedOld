package com.tms.threed.jpgGen.jpgGen2;

import java.io.File;
import java.io.FilenameFilter;

import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;

public class PreAndPostProcessor {

	protected final static String BACKUP_CHARACTERS = "_z";
	protected final static String NO_ACCESSORY_FILE_NAME = "model.xml.noaccy";
	protected final static String NORMAL_FILE_NAME = "model.xml";
	protected final static String TEMP_FILE_NAME = "model.xml.temp";
	
	public static void preProcess( Path pngRootFileSystem, SeriesKey seriesKey ) throws Exception
	{
		takeBackup(pngRootFileSystem, seriesKey);
		swapInNoAccessoryModelDotXml(pngRootFileSystem, seriesKey);
	}

	public static void postProcess( Path pngRootFileSystem, SeriesKey seriesKey ) throws Exception
	{
		restoreBackup(pngRootFileSystem, seriesKey);
		swapOutNoAccessoryModelDotXml(pngRootFileSystem, seriesKey);
	}

	
	private static void swapInNoAccessoryModelDotXml(Path pngRootFileSystem,
			SeriesKey seriesKey) {
		File pngFolder = new File( pngRootFileSystem.toString() );
		
		File currentFile = new File( pngFolder, new Integer(seriesKey.getYear()).toString() );
		
		if( currentFile.exists() )
		{
			currentFile = new File( currentFile, seriesKey.getName() );
			
			if( currentFile.exists() )
			{
				
				File noAccessoryModelDotXml = new File( currentFile, NO_ACCESSORY_FILE_NAME);
				File modelDotXml = new File( currentFile, NORMAL_FILE_NAME);
				File tempModelDotXml = new File( currentFile, TEMP_FILE_NAME);
				
				if( noAccessoryModelDotXml.isFile() )
				{
					if( modelDotXml.isFile() )
					{
						if( ! tempModelDotXml.isFile() )
						{
							modelDotXml.renameTo(tempModelDotXml);
							noAccessoryModelDotXml.renameTo(modelDotXml);
						}
					}
				}
				
			}
		}
	}

	private static void swapOutNoAccessoryModelDotXml(Path pngRootFileSystem,
			SeriesKey seriesKey) {
		File pngFolder = new File( pngRootFileSystem.toString() );
		
		File currentFile = new File( pngFolder, new Integer(seriesKey.getYear()).toString() );
		
		if( currentFile.exists() )
		{
			currentFile = new File( currentFile, seriesKey.getName() );
			
			if( currentFile.exists() )
			{
				
				File noAccessoryModelDotXml = new File( currentFile, NO_ACCESSORY_FILE_NAME);
				File modelDotXml = new File( currentFile, NORMAL_FILE_NAME);
				File tempModelDotXml = new File( currentFile, TEMP_FILE_NAME);
				
				if( tempModelDotXml.isFile() )
				{
					if( modelDotXml.isFile() )
					{
						if( ! noAccessoryModelDotXml.isFile() )
						{
							modelDotXml.renameTo(noAccessoryModelDotXml);
							tempModelDotXml.renameTo(modelDotXml);
						}
					}
				}
				
			}
		}
	}
	
	private static void takeBackup(Path pngRootFileSystem, SeriesKey seriesKey) {
		File pngFolder = new File( pngRootFileSystem.toString() );
		
		File currentFile = new File( pngFolder, new Integer(seriesKey.getYear()).toString() );
		
		if( currentFile.exists() )
		{
			currentFile = new File( currentFile, seriesKey.getName() );
			
			if( currentFile.exists() )
			{
				File backupFolder = new File( currentFile, "backup");
				if( ! backupFolder.isDirectory() )
				{
					backupFolder.mkdir();
				}
				
				
				File[] listFiles = currentFile.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return new File( dir, name).isDirectory() && !name.equals("backup");
					}
				});
				
				for( File child : listFiles )
				{
					File backupCopy = new File( backupFolder, child.getName() );
					if( ! backupCopy.exists() )
					{
						backupCopy.mkdir();
					}
					
					
					File[] zFolders = child.listFiles(new FilenameFilter() {
						
						@Override
						public boolean accept(File dir, String name) {
							return name.contains(BACKUP_CHARACTERS);
						}
					});
					
					for( File zFolder : zFolders)
					{
						File zFolderBackup = new File( backupCopy, zFolder.getName() );
						zFolder.renameTo(zFolderBackup);
					}
				}
				
			}
		}
	}
	
	
	
	private static void restoreBackup(Path pngRootFileSystem,
			SeriesKey seriesKey) {
		File pngFolder = new File( pngRootFileSystem.toString() );
		
		File currentFile = new File( pngFolder, new Integer(seriesKey.getYear()).toString() );
		
		if( currentFile.exists() )
		{
			currentFile = new File( currentFile, seriesKey.getName() );
			
			if( currentFile.exists() )
			{
				File backupFolder = new File( currentFile, "backup");
				if( backupFolder.isDirectory() )
				{
					File[] listFiles = backupFolder.listFiles();
					
					for( File child : listFiles )
					{
						File backupCopy = new File( backupFolder, child.getName() );
						
						File[] zFolders = backupCopy.listFiles(new FilenameFilter() {
							
							@Override
							public boolean accept(File dir, String name) {
								return name.contains("_z");
							}
						});
						
						for( File zFolder : zFolders)
						{
							File zFolderRestore = new File( new File(currentFile, child.getName()), zFolder.getName() );
							zFolder.renameTo(zFolderRestore);
						}
					}
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		Path pngRootFileSystem = new Path("/temp/tmsConfig/threed_framework/pngs");
		SeriesKey avalon2011 = SeriesKey.AVALON_2011;
		PreAndPostProcessor.preProcess(pngRootFileSystem, avalon2011);
		PreAndPostProcessor.postProcess(pngRootFileSystem, avalon2011);
	}
}
