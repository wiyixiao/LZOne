/*
* Copyright 2010 Srikanth Reddy Lingala  
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
* http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, 
* software distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
*/

package net.lingala.zip4j.util;

public interface InternalZipConstants {
	
	/*
     * Header signatures
     */
	// Whenever a new Signature is added here, make sure to add it
	// in Zip4jUtil.getAllHeaderSignatures()
    long LOCSIG = 0x04034b50L;	// "PK\003\004"
    long EXTSIG = 0x08074b50L;	// "PK\007\008"
    long CENSIG = 0x02014b50L;	// "PK\001\002"
    long ENDSIG = 0x06054b50L;	// "PK\005\006"
    long DIGSIG = 0x05054b50L;
    long ARCEXTDATREC = 0x08064b50L;
    long SPLITSIG = 0x08074b50L;
    long ZIP64ENDCENDIRLOC = 0x07064b50L;
    long ZIP64ENDCENDIRREC = 0x06064b50;
    int EXTRAFIELDZIP64LENGTH = 0x0001;
    int AESSIG = 0x9901;

    /*
     * Header sizes in bytes (including signatures)
     */
    int LOCHDR = 30;	// LOC header size
    int EXTHDR = 16;	// EXT header size
    int CENHDR = 46;	// CEN header size
    int ENDHDR = 22;	// END header size

    /*
     * Local file (LOC) header field offsets
     */
    int LOCVER = 4;	// version needed to extract
    int LOCFLG = 6;	// general purpose bit flag
    int LOCHOW = 8;	// compression method
    int LOCTIM = 10;	// modification time
    int LOCCRC = 14;	// uncompressed file crc-32 value
    int LOCSIZ = 18;	// compressed size
    int LOCLEN = 22;	// uncompressed size
    int LOCNAM = 26;	// filename length
    int LOCEXT = 28;	// extra field length

    /*
     * Extra local (EXT) header field offsets
     */
    int EXTCRC = 4;	// uncompressed file crc-32 value
    int EXTSIZ = 8;	// compressed size
    int EXTLEN = 12;	// uncompressed size

    /*
     * Central directory (CEN) header field offsets
     */
    int CENVEM = 4;	// version made by
    int CENVER = 6;	// version needed to extract
    int CENFLG = 8;	// encrypt, decrypt flags
    int CENHOW = 10;	// compression method
    int CENTIM = 12;	// modification time
    int CENCRC = 16;	// uncompressed file crc-32 value
    int CENSIZ = 20;	// compressed size
    int CENLEN = 24;	// uncompressed size
    int CENNAM = 28;	// filename length
    int CENEXT = 30;	// extra field length
    int CENCOM = 32;	// comment length
    int CENDSK = 34;	// disk number start
    int CENATT = 36;	// internal file attributes
    int CENATX = 38;	// external file attributes
    int CENOFF = 42;	// LOC header offset

    /*
     * End of central directory (END) header field offsets
     */
    int ENDSUB = 8;	// number of entries on this disk
    int ENDTOT = 10;	// total number of entries
    int ENDSIZ = 12;	// central directory size in bytes
    int ENDOFF = 16;	// offset of first CEN header
    int ENDCOM = 20;	// zip file comment length
    
    int STD_DEC_HDR_SIZE = 12;
    
    //AES Constants
    int AES_AUTH_LENGTH = 10;
    int AES_BLOCK_SIZE = 16;
    
    int MIN_SPLIT_LENGTH = 65536;
    
    long ZIP_64_LIMIT = 4294967295L;
	
	String OFFSET_CENTRAL_DIR = "offsetCentralDir";
	
	String VERSION = "1.3.2";
	
	int MODE_ZIP = 1;
	
	int MODE_UNZIP = 2;
	
	String WRITE_MODE = "rw";
	
	String READ_MODE = "r";
	
	int BUFF_SIZE = 1024 * 4;
	
	int FILE_MODE_NONE = 0;
	
	int FILE_MODE_READ_ONLY = 1;
	
	int FILE_MODE_HIDDEN = 2;
	
	int FILE_MODE_ARCHIVE = 32;
	
	int FILE_MODE_READ_ONLY_HIDDEN = 3;
	
	int FILE_MODE_READ_ONLY_ARCHIVE = 33;
	
	int FILE_MODE_HIDDEN_ARCHIVE = 34;
	
	int FILE_MODE_READ_ONLY_HIDDEN_ARCHIVE = 35;
	
	int FILE_MODE_SYSTEM = 38;
	
	int FOLDER_MODE_NONE = 16;
	
	int FOLDER_MODE_HIDDEN = 18;

	int FOLDER_MODE_ARCHIVE = 48;
	
	int FOLDER_MODE_HIDDEN_ARCHIVE = 50;
	
	// Update local file header constants
	// This value holds the number of bytes to skip from
	// the offset of start of local header
    int UPDATE_LFH_CRC = 14;
	
	int UPDATE_LFH_COMP_SIZE = 18;
	
	int UPDATE_LFH_UNCOMP_SIZE = 22;
	
	int LIST_TYPE_FILE = 1;
	
	int LIST_TYPE_STRING = 2;
	
	int UFT8_NAMES_FLAG = 1 << 11;
	
	String CHARSET_UTF8 = "UTF8";
	
	String CHARSET_CP850 = "Cp850";
	
	String CHARSET_COMMENTS_DEFAULT = "windows-1254";
	
	String CHARSET_DEFAULT = System.getProperty("file.encoding");
	
	String FILE_SEPARATOR = System.getProperty("file.separator");
	
	String ZIP_FILE_SEPARATOR = "/";
	
	String THREAD_NAME = "Zip4j";
	
	int MAX_ALLOWED_ZIP_COMMENT_LENGTH = 0xFFFF;
}
