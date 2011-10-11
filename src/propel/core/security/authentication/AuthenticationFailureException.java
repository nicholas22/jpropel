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
package propel.core.security.authentication;

/**
 * Exception thrown when authentication fails after the handshake.
 * This indicates that the client has failed to authenticate with the server.
 */
public class AuthenticationFailureException
		extends Exception
{
  private static final long serialVersionUID = 4211274557959013068L;

  /**
	 * Default constructor
	 */
	public AuthenticationFailureException()
	{
	}

	/**
	 * Conversion constructor
	 */
	public AuthenticationFailureException(String msg)
	{
		super(msg);
	}

	/**
	 * Conversion constructor
	 */
	public AuthenticationFailureException(String msg, Throwable inner)
	{
		super(msg, inner);
	}
}
