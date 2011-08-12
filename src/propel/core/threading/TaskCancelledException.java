/*
 ///////////////////////////////////////////////////////////
 //  This file is part of Propel.
 //
 //  Propel is free software: you can redistribute it and/or modify
 //  it under the terms of the GNU Lesser General Public License as published by
 //  the Free Software Foundation, either version 3 of the License, or
 //  (at your option) any later version.
 //
 //  Propel is distributed in the hope that it will be useful,
 //  but WITHOUT ANY WARRANTY; without even the implied warranty of
 //  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 //  GNU Lesser General Public License for more details.
 //
 //  You should have received a copy of the GNU Lesser General Public License
 //  along with Propel.  If not, see <http://www.gnu.org/licenses/>.
 ///////////////////////////////////////////////////////////
 //  Authored by: Nikolaos Tountas -> salam.kaser-at-gmail.com
 ///////////////////////////////////////////////////////////
 */

package propel.core.threading;

import propel.core.common.CONSTANT;
import propel.core.utils.StringUtils;

/**
    * An exception thrown within a task when the task is cancelled.
    */
    public class TaskCancelledException
		 extends Exception
    {
        /**
        * Default constructor
        */
        public TaskCancelledException()
        {
        }

        /**
        * Conversion constructor
        */
        public TaskCancelledException(String message)
        {
			super(message);
        }

        /**
        * Conversion constructor
        */
        public TaskCancelledException(String message, Throwable innerException)
        {
			super(message, innerException);
        }

        /**
        * Conversion constructor
        */
        public TaskCancelledException(ICancellation canceller, ThreadDeath e)
        {
			this("Task was cancelled" + (!StringUtils.isNullOrEmpty(canceller.getSource()) ? " by " + canceller.getSource() : CONSTANT.EMPTY_STRING) + (!StringUtils.isNullOrEmpty(canceller.getReason()) ? ", reason: " + canceller.getReason() : CONSTANT.EMPTY_STRING) + ".", e);
        }
    }
