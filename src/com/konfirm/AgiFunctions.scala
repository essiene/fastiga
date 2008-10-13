package com.konfirm

import java.io._
import java.util.Vector

class AgiFunctions {
	def getAsteriskHeaders(is: InputStream): Unit = {
		var ch: Int = 0
		var sb: StringBuffer = new StringBuffer()
		var inVector: Vector[Int] = new Vector()

		var streamHasMore = true
		while(streamHasMore) {
			ch = is.read()
			inVector.add(ch)

			val thisChar: Char = ch.asInstanceOf[char]
			sb.append(thisChar)

			if(isEOF(inVector, 2)) streamHasMore = false;
		}
		println("---------Asterisk Headers-----------\n" + sb.toString());
	}

	def isEOF(inVector: Vector[Int], noOfChars: Int): Boolean = {
		val size = inVector.size();
		if(size < 4) return false
        if(noOfChars == 2) {
		    if((inVector.get(size - 2) == 10) && (inVector.get(size - 1) == 10))
			    return true
        } else {
            if(inVector.get(size - 1) == 10)
                return true
        }
		return false
	}
}
