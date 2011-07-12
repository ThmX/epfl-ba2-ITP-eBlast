/*
 * This file is part of eBlast Project.
 *
 * Copyright (c) 2011 eBlast
 *
 * eBlast is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eBlast is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eBlast.  If not, see <http://www.gnu.org/licenses/>.
 */

package eblast.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import eblast.bencoding.InvalidBEncodingException;
import eblast.checksum.Hash;
import eblast.metainfo.AnnounceList;
import eblast.metainfo.Info;
import eblast.metainfo.MetaInfo;
import eblast.metainfo.MetaInfoException;
import eblast.metainfo.MetaInfoReader;
import eblast.metainfo.MetaInfoRequiredKeyException;

public class MetaInfoTest {
    
    @Test
    public void testMetaInfoLocal() throws MetaInfoException, FileNotFoundException, IOException {
    	MetaInfo metainfo = MetaInfoReader.openMetaInfo("trunk/data/LePetitPrince.torrent");

    	assertEquals(metainfo.getAnnounce(), "http://icsinsrv1.epfl.ch:6969/");
    	assertEquals(metainfo.getComment(), "Le petit prince est l'oeuvre la plus connue d'Antoine de Saint-Exupery");
    	assertEquals(metainfo.getCreatedBy(), "Transmission/2.20b3 (11793)");
    	assertEquals(metainfo.getCreationDate().toString(), "Tue Feb 08 17:02:43 CET 2011");
    	assertEquals(metainfo.getEncoding(), "UTF-8");
    	assertEquals(metainfo.getInfoHash(), new Hash("cd4cc08c10070aeec0b0b2c5e40bc1c45111820d"));
    	
    	Info info = metainfo.getInfo();
    	assertEquals(info.getPieceLength(), 32768);
    	assertEquals(info.getName(), "st_exupery_le_petit_prince.pdf");
    	assertEquals(info.getFiles().size(), 1);
    	assertFalse(info.isPrivate());
    }
    
    
    @Test
    public void testMetaInfoURLSingle() throws MetaInfoException, MalformedURLException, IOException {
		String url = "http://torrents.thepiratebay.org/6200621/The_Big_Bang_Theory_S04E17_The_Toast_Derivation_HDTV_XviD-FQM_[e.6200621.TPB.torrent";
		
		MetaInfo metainfo = MetaInfoReader.openMetaInfoFromURL(url);
		assertEquals(metainfo.getAnnounce(), "http://tracker.thepiratebay.org/announce");
    	assertEquals(metainfo.getComment(), "Torrent downloaded from http://thepiratebay.org");
    	assertNull(metainfo.getCreatedBy());
    	assertEquals(metainfo.getCreationDate().toString(), "Fri Feb 25 02:38:17 CET 2011");
    	assertNull(metainfo.getEncoding());
    	assertEquals(metainfo.getInfoHash(), new Hash("bdab1d16b75957751a4168e9e5d98cbf3f9d9c8f"));
    	
    	Info info = metainfo.getInfo();
    	assertEquals(info.getPieceLength(), 524288);
    	assertEquals(info.getName(), "The.Big.Bang.Theory.S04E17.The.Toast.Derivation.HDTV.XviD-FQM.avi");
    	assertEquals(info.getFiles().size(), 1);
    	assertFalse(info.isPrivate());
    }
    
    @Test
    public void testMetaInfoURLMulti() throws MetaInfoException, MalformedURLException, IOException {
    	String url = "http://torrents.thepiratebay.org/4433962/Sum41-Underclass_Hero-(Retail)-2007-HHI.4433962.TPB.torrent";
		
    	MetaInfo metainfo = MetaInfoReader.openMetaInfoFromURL(url);
		assertEquals(metainfo.getAnnounce(), "http://tracker.thepiratebay.org/announce");
    	assertNull(metainfo.getComment());
    	assertEquals(metainfo.getCreatedBy(), "uTorrent/1800");
    	assertEquals(metainfo.getCreationDate().toString(), "Wed Oct 08 00:58:33 CEST 2008");
    	assertEquals(metainfo.getEncoding(), "UTF-8");
    	assertEquals(metainfo.getInfoHash(), new Hash("066d6f983f825650e6910cc8179b96904beb422d"));
    	
    	Info info = metainfo.getInfo();
    	assertEquals(info.getPieceLength(), 131072);
    	assertEquals(info.getName(), "Sum41-Underclass_Hero-(Retail)-2007-HHI");
    	assertEquals(info.getFiles().size(), 18);
    	assertFalse(info.isPrivate());
    }
    
    // Should throw an exception about a missing required key.
    @Test(expected = MetaInfoRequiredKeyException.class)
    public void testMetaInfoLocalMetaInfoExceptionRequired() throws MetaInfoException, FileNotFoundException, IOException {
    	String filename = "trunk/data/MetaInfoExceptionRequired.torrent";
    	MetaInfoReader.openMetaInfo(filename);
    }
    
    // Should throw an exception about SHA-1 key length
    @Test(expected = MetaInfoException.class)
    public void testMetaInfoLocalMetaInfoExceptionSHA1() throws MetaInfoException, FileNotFoundException, IOException {
    	String filename = "trunk/data/MetaInfoExceptionSHA1.torrent";
    	MetaInfoReader.openMetaInfo(filename);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void testMetaInfoLocalFileNotFoundException() throws MetaInfoException, FileNotFoundException, IOException {
    	String filename = "trunk/data/Unkown.torrent";
    	MetaInfoReader.openMetaInfo(filename);
    }
    
    @SuppressWarnings("unused")
	private void printMetaInfo(MetaInfo metainfo) throws InvalidBEncodingException {
		System.out.println("Announce = " + metainfo.getAnnounce());
		System.out.println("Comment = " + metainfo.getComment());
		System.out.println("Created By = " + metainfo.getCreatedBy());
		System.out.println("Creation Date = " + metainfo.getCreationDate());
		System.out.println("Encoding = " + metainfo.getEncoding());		
		System.out.println();
		
		AnnounceList announceList = metainfo.getAnnounceList();
		if (announceList != null) {
			for (int i=0; i<announceList.size(); i++) {
				System.out.println(announceList.get(i));
			}
			System.out.println();
		}
		
		Info info = metainfo.getInfo();
		System.out.println("Info_hash = " + metainfo.getInfoHash().toHexString());
		System.out.println("Piece Length = " + info.getPieceLength());
		System.out.println("Pieces = " + info.getPiecesHashes());
		System.out.println("Name = " + info.getName());
		System.out.println("Files:");
		for (eblast.io.TorrentFile file : info.getFiles()) {
			System.out.println("\t" + file.getPath());
		}
		System.out.println("Private = " + info.isPrivate());
		System.out.println("-----------------------------------------");
    }
}