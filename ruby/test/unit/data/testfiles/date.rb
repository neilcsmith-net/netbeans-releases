#
# date.rb - date and time library
#
# Author: Tadayoshi Funaba 1998-2006
#
# Documentation: William Webber <william@williamwebber.com>
#
# == Overview
#
# This file provides two classes for working with
# dates and times.
#
# The first class, Date, represents dates. 
# It works with years, months, weeks, and days.
# See the Date class documentation for more details.
#
# The second, DateTime, extends Date to include hours,
# minutes, seconds, and fractions of a second.  It
# provides basic support for time zones.  See the
# DateTime class documentation for more details.
#
# === Ways of calculating the date.
#
# In common usage, the date is reckoned in years since or
# before the Common Era (CE/BCE, also known as AD/BC), then
# as a month and day-of-the-month within the current year.
# This is known as the *Civil* *Date*, and abbreviated
# as +civil+ in the Date class.
#
# Instead of year, month-of-the-year,  and day-of-the-month,
# the date can also be reckoned in terms of year and
# day-of-the-year.  This is known as the *Ordinal* *Date*,
# and is abbreviated as +ordinal+ in the Date class.  (Note
# that referring to this as the Julian date is incorrect.)
#
# The date can also be reckoned in terms of year, week-of-the-year,
# and day-of-the-week.  This is known as the *Commercial*
# *Date*, and is abbreviated as +commercial+ in the
# Date class.  The commercial week runs Monday (day-of-the-week
# 1) to Sunday (day-of-the-week 7), in contrast to the civil
# week which runs Sunday (day-of-the-week 0) to Saturday
# (day-of-the-week 6).  The first week of the commercial year
# starts on the Monday on or before January 1, and the commercial
# year itself starts on this Monday, not January 1.
#
# For scientific purposes, it is convenient to refer to a date
# simply as a day count, counting from an arbitrary initial
# day.  The date first chosen for this was January 1, 4713 BCE.
# A count of days from this date is the *Julian* *Day* *Number*
# or *Julian* *Date*, which is abbreviated as +jd+ in the
# Date class.  This is in local time, and counts from midnight
# on the initial day.  The stricter usage is in UTC, and counts
# from midday on the initial day.  This is referred to in the
# Date class as the *Astronomical* *Julian* *Day* *Number*, and
# abbreviated as +ajd+.  In the Date class, the Astronomical
# Julian Day Number includes fractional days.
#
# Another absolute day count is the *Modified* *Julian* *Day*
# *Number*, which takes November 17, 1858 as its initial day.
# This is abbreviated as +mjd+ in the Date class.  There
# is also an *Astronomical* *Modified* *Julian* *Day* *Number*,
# which is in UTC and includes fractional days.  This is
# abbreviated as +amjd+ in the Date class.  Like the Modified
# Julian Day Number (and unlike the Astronomical Julian
# Day Number), it counts from midnight.
#
# Alternative calendars such as the Chinese Lunar Calendar,
# the Islamic Calendar, or the French Revolutionary Calendar
# are not supported by the Date class; nor are calendars that
# are based on an Era different from the Common Era, such as
# the Japanese Imperial Calendar or the Republic of China
# Calendar.
#
# === Calendar Reform
#
# The standard civil year is 365 days long.  However, the
# solar year is fractionally longer than this.  To account
# for this, a *leap* *year* is occasionally inserted.  This
# is a year with 366 days, the extra day falling on February 29. 
# In the early days of the civil calendar, every fourth
# year without exception was a leap year.  This way of
# reckoning leap years is the *Julian* *Calendar*.
#
# However, the solar year is marginally shorter than 365 1/4
# days, and so the *Julian* *Calendar* gradually ran slow
# over the centuries.  To correct this, every 100th year
# (but not every 400th year) was excluded as a leap year.
# This way of reckoning leap years, which we use today, is
# the *Gregorian* *Calendar*.
#
# The Gregorian Calendar was introduced at different times
# in different regions.  The day on which it was introduced
# for a particular region is the *Day* *of* *Calendar*
# *Reform* for that region.  This is abbreviated as +sg+
# (for Start of Gregorian calendar) in the Date class.
#
# Two such days are of particular
# significance.  The first is October 15, 1582, which was
# the Day of Calendar Reform for Italy and most Catholic
# countries.  The second is September 14, 1752, which was
# the Day of Calendar Reform for England and its colonies
# (including what is now the United States).  These two
# dates are available as the constants Date::ITALY and
# Date::ENGLAND, respectively.  (By comparison, Germany and
# Holland, less Catholic than Italy but less stubborn than
# England, changed over in 1698; Sweden in 1753; Russia not
# till 1918, after the Revolution; and Greece in 1923.  Many
# Orthodox churches still use the Julian Calendar.  A complete
# list of Days of Calendar Reform can be found at
# http://www.polysyllabic.com/GregConv.html.)
#
# Switching from the Julian to the Gregorian calendar
# involved skipping a number of days to make up for the
# accumulated lag, and the later the switch was (or is)
# done, the more days need to be skipped.  So in 1582 in Italy,
# 4th October was followed by 15th October, skipping 10 days; in 1752
# in England, 2nd September was followed by 14th September, skipping
# 11 days; and if I decided to switch from Julian to Gregorian
# Calendar this midnight, I would go from 27th July 2003 (Julian)
# today to 10th August 2003 (Gregorian) tomorrow, skipping
# 13 days.  The Date class is aware of this gap, and a supposed
# date that would fall in the middle of it is regarded as invalid.
#
# The Day of Calendar Reform is relevant to all date representations
# involving years.  It is not relevant to the Julian Day Numbers,
# except for converting between them and year-based representations.
#
# In the Date and DateTime classes, the Day of Calendar Reform or
# +sg+ can be specified a number of ways.  First, it can be as
# the Julian Day Number of the Day of Calendar Reform.  Second,
# it can be using the constants Date::ITALY or Date::ENGLAND; these
# are in fact the Julian Day Numbers of the Day of Calendar Reform
# of the respective regions.  Third, it can be as the constant
# Date::JULIAN, which means to always use the Julian Calendar.
# Finally, it can be as the constant Date::GREGORIAN, which means
# to always use the Gregorian Calendar.
#
# Note: in the Julian Calendar, New Years Day was March 25.  The
# Date class does not follow this convention.
#
# === Time Zones
#
# DateTime objects support a simple representation
# of time zones.  Time zones are represented as an offset
# from UTC, as a fraction of a day.  This offset is the
# how much local time is later (or earlier) than UTC.
# UTC offset 0 is centred on England (also known as GMT). 
# As you travel east, the offset increases until you
# reach the dateline in the middle of the Pacific Ocean;
# as you travel west, the offset decreases.  This offset
# is abbreviated as +of+ in the Date class.
#
# This simple representation of time zones does not take
# into account the common practice of Daylight Savings
# Time or Summer Time.
#
# Most DateTime methods return the date and the
# time in local time.  The two exceptions are
# #ajd() and #amjd(), which return the date and time
# in UTC time, including fractional days.
#
# The Date class does not support time zone offsets, in that
# there is no way to create a Date object with a time zone.
# However, methods of the Date class when used by a
# DateTime instance will use the time zone offset of this
# instance.
#
# == Examples of use
#
# === Print out the date of every Sunday between two dates.
#
#     def print_sundays(d1, d2)
#         d1 +=1 while (d1.wday != 0)
#         d1.step(d2, 7) do |date|
#             puts "#{Date::MONTHNAMES[date.mon]} #{date.day}"
#         end
#     end
#
#     print_sundays(Date::civil(2003, 4, 8), Date::civil(2003, 5, 23))
#
# === Calculate how many seconds to go till midnight on New Year's Day.
#
#     def secs_to_new_year(now = DateTime::now())
#         new_year = DateTime.new(now.year + 1, 1, 1)
#         dif = new_year - now
#         hours, mins, secs, ignore_fractions = Date::day_fraction_to_time(dif)
#         return hours * 60 * 60 + mins * 60 + secs
#     end
#
#     puts secs_to_new_year()

require 'rational'
require 'date/format'

# Class representing a date.
#
# See the documentation to the file date.rb for an overview.
#
# Internally, the date is represented as an Astronomical
# Julian Day Number, +ajd+.  The Day of Calendar Reform, +sg+, is
# also stored, for conversions to other date formats.  (There
# is also an +of+ field for a time zone offset, but this
# is only for the use of the DateTime subclass.)
#
# A new Date object is created using one of the object creation
# class methods named after the corresponding date format, and the
# arguments appropriate to that date format; for instance,
# Date::civil() (aliased to Date::new()) with year, month,
# and day-of-month, or Date::ordinal() with year and day-of-year.
# All of these object creation class methods also take the
# Day of Calendar Reform as an optional argument.
#
# Date objects are immutable once created.
#
# Once a Date has been created, date values
# can be retrieved for the different date formats supported
# using instance methods.  For instance, #mon() gives the
# Civil month, #cwday() gives the Commercial day of the week,
# and #yday() gives the Ordinal day of the year.  Date values
# can be retrieved in any format, regardless of what format
# was used to create the Date instance.
#
# The Date class includes the Comparable module, allowing
# date objects to be compared and sorted, ranges of dates
# to be created, and so forth.
class Date

  include Comparable

  # Full month names, in English.  Months count from 1 to 12; a
  # month's numerical representation indexed into this array
  # gives the name of that month (hence the first element is nil).
  MONTHNAMES = [nil] + %w(January February March April May June July
			  August September October November December)

  # Full names of days of the week, in English.  Days of the week
  # count from 0 to 6 (except in the commercial week); a day's numerical
  # representation indexed into this array gives the name of that day.
  DAYNAMES = %w(Sunday Monday Tuesday Wednesday Thursday Friday Saturday)

  # Abbreviated month names, in English.
  ABBR_MONTHNAMES = [nil] + %w(Jan Feb Mar Apr May Jun
			       Jul Aug Sep Oct Nov Dec)

  # Abbreviated day names, in English.
  ABBR_DAYNAMES = %w(Sun Mon Tue Wed Thu Fri Sat)

  # The Julian Day Number of the Day of Calendar Reform for Italy
  # and the Catholic countries.
  ITALY     = 2299161 # 1582-10-15

  # The Julian Day Number of the Day of Calendar Reform for England
  # and her Colonies.
  ENGLAND   = 2361222 # 1752-09-14

  # A constant used to indicate that a Date should always use the
  # Julian calendar.
  JULIAN    = false

  # A constant used to indicate that a Date should always use the
  # Gregorian calendar.
  GREGORIAN = true

  # Does a given Julian Day Number fall inside the old-style (Julian)
  # calendar?
  #
  # +jd+ is the Julian Day Number in question. +sg+ may be Date::GREGORIAN,
  # in which case the answer is false; it may be Date::JULIAN, in which case
  # the answer is true; or it may a number representing the Day of
  # Calendar Reform. Date::ENGLAND and Date::ITALY are two possible such
  # days.
  def self.os? (jd, sg)
    case sg
    when Numeric; jd < sg
    else;         not sg
    end
  end

  # Does a given Julian Day Number fall inside the new-style (Gregorian)
  # calendar?
  #
  # The reverse of self.os?  See the documentation for that method for
  # more details.
  def self.ns? (jd, sg) not os?(jd, sg) end

  # Convert a Civil Date to a Julian Day Number.
  # +y+, +m+, and +d+ are the year, month, and day of the
  # month.  +sg+ specifies the Day of Calendar Reform.
  #
  # Returns the corresponding Julian Day Number.
  def self.civil_to_jd(y, m, d, sg=GREGORIAN)
    if m <= 2
      y -= 1
      m += 12
    end
    a = (y / 100.0).floor
    b = 2 - a + (a / 4.0).floor
    jd = (365.25 * (y + 4716)).floor +
      (30.6001 * (m + 1)).floor +
      d + b - 1524
    if os?(jd, sg)
      jd -= b
    end
    jd
  end

  # Convert a Julian Day Number to a Civil Date.  +jd+ is
  # the Julian Day Number. +sg+ specifies the Day of
  # Calendar Reform.
  #
  # Returns the corresponding [year, month, day_of_month]
  # as a three-element array.
  def self.jd_to_civil(jd, sg=GREGORIAN)
    if os?(jd, sg)
      a = jd
    else
      x = ((jd - 1867216.25) / 36524.25).floor
      a = jd + 1 + x - (x / 4.0).floor
    end
    b = a + 1524
    c = ((b - 122.1) / 365.25).floor
    d = (365.25 * c).floor
    e = ((b - d) / 30.6001).floor
    dom = b - d - (30.6001 * e).floor
    if e <= 13
      m = e - 1
      y = c - 4716
    else
      m = e - 13
      y = c - 4715
    end
    return y, m, dom
  end

  # Convert an Ordinal Date to a Julian Day Number.
  #
  # +y+ and +d+ are the year and day-of-year to convert.
  # +sg+ specifies the Day of Calendar Reform.
  #
  # Returns the corresponding Julian Day Number.
  def self.ordinal_to_jd(y, d, sg=GREGORIAN)
    civil_to_jd(y, 1, d, sg)
  end

  # Convert a Julian Day Number to an Ordinal Date.
  #
  # +jd+ is the Julian Day Number to convert.
  # +sg+ specifies the Day of Calendar Reform.
  #
  # Returns the corresponding Ordinal Date as
  # [year, day_of_year]
  def self.jd_to_ordinal(jd, sg=GREGORIAN)
    y = jd_to_civil(jd, sg)[0]
    doy = jd - civil_to_jd(y - 1, 12, 31, ns?(jd, sg))
    return y, doy
  end

  # Convert a Julian Day Number to a Commercial Date
  #
  # +jd+ is the Julian Day Number to convert.
  # +sg+ specifies the Day of Calendar Reform.
  #
  # Returns the corresponding Commercial Date as
  # [commercial_year, week_of_year, day_of_week]
  def self.jd_to_commercial(jd, sg=GREGORIAN)
    ns = ns?(jd, sg)
    a = jd_to_civil(jd - 3, ns)[0]
    y = if jd >= commercial_to_jd(a + 1, 1, 1, ns) then a + 1 else a end
    w = 1 + ((jd - commercial_to_jd(y, 1, 1, ns)) / 7).floor
    d = (jd + 1) % 7
    if d.zero? then d = 7 end
    return y, w, d
  end

  # Convert a Commercial Date to a Julian Day Number.
  #
  # +y+, +w+, and +d+ are the (commercial) year, week of the year,
  # and day of the week of the Commercial Date to convert.
  # +sg+ specifies the Day of Calendar Reform.
  def self.commercial_to_jd(y, w, d, ns=GREGORIAN)
    jd = civil_to_jd(y, 1, 4, ns)
    (jd - (((jd - 1) + 1) % 7)) +
      7 * (w - 1) +
      (d - 1)
  end

  %w(self.clfloor clfloor).each do |name|
    module_eval <<-"end;"
      def #{name}(x, y=1)
	q, r = x.divmod(y)
	q = q.to_i
	return q, r
      end
    end;
  end

  private_class_method :clfloor
  private              :clfloor


  # Convert an Astronomical Julian Day Number to a (civil) Julian
  # Day Number.
  #
  # +ajd+ is the Astronomical Julian Day Number to convert. 
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  #
  # Returns the (civil) Julian Day Number as [day_number,
  # fraction] where +fraction+ is always 1/2.
  def self.ajd_to_jd(ajd, of=0) clfloor(ajd + of + 1.to_r/2) end

  # Convert a (civil) Julian Day Number to an Astronomical Julian
  # Day Number.
  #
  # +jd+ is the Julian Day Number to convert, and +fr+ is a
  # fractional day. 
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  #
  # Returns the Astronomical Julian Day Number as a single
  # numeric value.
  def self.jd_to_ajd(jd, fr, of=0) jd + fr - of - 1.to_r/2 end

  # Convert a fractional day +fr+ to [hours, minutes, seconds,
  # fraction_of_a_second]
  def self.day_fraction_to_time(fr)
    h,   fr = clfloor(fr, 1.to_r/24)
    min, fr = clfloor(fr, 1.to_r/1440)
    s,   fr = clfloor(fr, 1.to_r/86400)
    return h, min, s, fr
  end

  # Convert an +h+ hour, +min+ minutes, +s+ seconds period
  # to a fractional day.
  def self.time_to_day_fraction(h, min, s)
    h.to_r/24 + min.to_r/1440 + s.to_r/86400
  end

  # Convert an Astronomical Modified Julian Day Number to an
  # Astronomical Julian Day Number.
  def self.amjd_to_ajd(amjd) amjd + 4800001.to_r/2 end

  # Convert an Astronomical Julian Day Number to an
  # Astronomical Modified Julian Day Number.
  def self.ajd_to_amjd(ajd) ajd - 4800001.to_r/2 end

  # Convert a Modified Julian Day Number to a Julian
  # Day Number.
  def self.mjd_to_jd(mjd) mjd + 2400001 end

  # Convert a Julian Day Number to a Modified Julian Day
  # Number.
  def self.jd_to_mjd(jd) jd - 2400001 end

  # Convert a count of the number of days since the adoption
  # of the Gregorian Calendar (in Italy) to a Julian Day Number.
  def self.ld_to_jd(ld) ld + 2299160 end

  # Convert a Julian Day Number to the number of days since
  # the adoption of the Gregorian Calendar (in Italy).
  def self.jd_to_ld(jd) jd - 2299160 end

  # Convert a Julian Day Number to the day of the week.
  #
  # Sunday is day-of-week 0; Saturday is day-of-week 6.
  def self.jd_to_wday(jd) (jd + 1) % 7 end

  # Is a year a leap year in the Julian calendar?
  #
  # All years divisible by 4 are leap years in the Julian calendar.
  def self.julian_leap? (y) y % 4 == 0 end

  # Is a year a leap year in the Gregorian calendar?
  #
  # All years divisible by 4 are leap years in the Gregorian calendar,
  # except for years divisible by 100 and not by 400.
  def self.gregorian_leap? (y) y % 4 == 0 and y % 100 != 0 or y % 400 == 0 end

  class << self; alias_method :leap?, :gregorian_leap? end
  class << self; alias_method :new0, :new end

  # Is +jd+ a valid Julian Day Number?
  #
  # If it is, returns it.  In fact, any value is treated as a valid
  # Julian Day Number.
  def self.valid_jd? (jd, sg=ITALY) jd end

  # Create a new Date object from a Julian Day Number.
  #
  # +jd+ is the Julian Day Number; if not specified, it defaults to
  # 0. 
  # +sg+ specifies the Day of Calendar Reform.
  def self.jd(jd=0, sg=ITALY)
    jd = valid_jd?(jd, sg)
    new0(jd_to_ajd(jd, 0, 0), 0, sg)
  end

  # Do the year +y+ and day-of-year +d+ make a valid Ordinal Date?
  # Returns the corresponding Julian Day Number if they do, or
  # nil if they don't.
  #
  # +d+ can be a negative number, in which case it counts backwards
  # from the end of the year (-1 being the last day of the year).
  # No year wraparound is performed, however, so valid values of
  # +d+ are -365 .. -1, 1 .. 365 on a non-leap-year,
  # -366 .. -1, 1 .. 366 on a leap year. 
  # A date falling in the period skipped in the Day of Calendar Reform
  # adjustment is not valid.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.valid_ordinal? (y, d, sg=ITALY)
    if d < 0
      ny, = clfloor(y + 1, 1)
      jd = ordinal_to_jd(ny, d + 1, sg)
      ns = ns?(jd, sg)
      return unless [y] == jd_to_ordinal(jd, sg)[0..0]
      return unless [ny, 1] == jd_to_ordinal(jd - d, ns)
    else
      jd = ordinal_to_jd(y, d, sg)
      return unless [y, d] == jd_to_ordinal(jd, sg)
    end
    jd
  end

  # Create a new Date object from an Ordinal Date, specified
  # by year +y+ and day-of-year +d+. +d+ can be negative,
  # in which it counts backwards from the end of the year.
  # No year wraparound is performed, however.  An invalid
  # value for +d+ results in an ArgumentError being raised.
  #
  # +y+ defaults to -4712, and +d+ to 1; this is Julian Day
  # Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.ordinal(y=-4712, d=1, sg=ITALY)
    unless jd = valid_ordinal?(y, d, sg)
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, 0, 0), 0, sg)
  end

  # Do year +y+, month +m+, and day-of-month +d+ make a
  # valid Civil Date?  Returns the corresponding Julian
  # Day Number if they do, nil if they don't.
  #
  # +m+ and +d+ can be negative, in which case they count
  # backwards from the end of the year and the end of the
  # month respectively.  No wraparound is performed, however,
  # and invalid values cause an ArgumentError to be raised.
  # A date falling in the period skipped in the Day of Calendar
  # Reform adjustment is not valid.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.valid_civil? (y, m, d, sg=ITALY)
    if m < 0
      m += 13
    end
    if d < 0
      ny, nm = clfloor(y * 12 + m, 12)
      nm,    = clfloor(nm + 1, 1)
      jd = civil_to_jd(ny, nm, d + 1, sg)
      ns = ns?(jd, sg)
      return unless [y, m] == jd_to_civil(jd, sg)[0..1]
      return unless [ny, nm, 1] == jd_to_civil(jd - d, ns)
    else
      jd = civil_to_jd(y, m, d, sg)
      return unless [y, m, d] == jd_to_civil(jd, sg)
    end
    jd
  end

  class << self; alias_method :valid_date?, :valid_civil? end

  # Create a new Date object for the Civil Date specified by
  # year +y+, month +m+, and day-of-month +d+.
  #
  # +m+ and +d+ can be negative, in which case they count
  # backwards from the end of the year and the end of the
  # month respectively.  No wraparound is performed, however,
  # and invalid values cause an ArgumentError to be raised.
  # can be negative
  #
  # +y+ defaults to -4712, +m+ to 1, and +d+ to 1; this is
  # Julian Day Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.civil(y=-4712, m=1, d=1, sg=ITALY)
    unless jd = valid_civil?(y, m, d, sg)
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, 0, 0), 0, sg)
  end

  class << self; alias_method :new, :civil end

  # Do year +y+, week-of-year +w+, and day-of-week +d+ make a
  # valid Commercial Date?  Returns the corresponding Julian
  # Day Number if they do, nil if they don't.
  #
  # Monday is day-of-week 1; Sunday is day-of-week 7.
  #
  # +w+ and +d+ can be negative, in which case they count
  # backwards from the end of the year and the end of the
  # week respectively.  No wraparound is performed, however,
  # and invalid values cause an ArgumentError to be raised.
  # A date falling in the period skipped in the Day of Calendar
  # Reform adjustment is not valid.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.valid_commercial? (y, w, d, sg=ITALY)
    if d < 0
      d += 8
    end
    if w < 0
      w = jd_to_commercial(commercial_to_jd(y + 1, 1, 1) + w * 7)[1]
    end
    jd = commercial_to_jd(y, w, d)
    return unless ns?(jd, sg)
    return unless [y, w, d] == jd_to_commercial(jd)
    jd
  end

  # Create a new Date object for the Commercial Date specified by
  # year +y+, week-of-year +w+, and day-of-week +d+.
  #
  # Monday is day-of-week 1; Sunday is day-of-week 7.
  #
  # +w+ and +d+ can be negative, in which case they count
  # backwards from the end of the year and the end of the
  # week respectively.  No wraparound is performed, however,
  # and invalid values cause an ArgumentError to be raised.
  #
  # +y+ defaults to 1582, +w+ to 41, and +d+ to 5, the Day of
  # Calendar Reform for Italy and the Catholic countries.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.commercial(y=1582, w=41, d=5, sg=ITALY)
    unless jd = valid_commercial?(y, w, d, sg)
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, 0, 0), 0, sg)
  end

  def self.new_with_hash(elem, sg)
    elem ||= {}
    y, m, d = elem.values_at(:year, :mon, :mday)
    if [y, m, d].include? nil
      raise ArgumentError, '3 elements of civil date are necessary'
    else
      civil(y, m, d, sg)
    end
  end

  private_class_method :new_with_hash

  # Create a new Date object by parsing from a String
  # according to a specified format.
  #
  # +str+ is a String holding a date representation.
  # +fmt+ is the format that the date is in.  See
  # date/format.rb for details on supported formats.
  #
  # The default +str+ is '-4712-01-01', and the default
  # +fmt+ is '%F', which means Year-Month-Day_of_Month.
  # This gives Julian Day Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  #
  # An ArgumentError will be raised if +str+ cannot be
  # parsed.
  def self.strptime(str='-4712-01-01', fmt='%F', sg=ITALY)
    elem = _strptime(str, fmt)
    new_with_hash(elem, sg)
  end

  # Create a new Date object by parsing from a String,
  # without specifying the format.
  #
  # +str+ is a String holding a date representation. 
  # +comp+ specifies whether to interpret 2-digit years
  # as 19XX (>= 69) or 20XX (< 69); the default is not to.
  # The method will attempt to parse a date from the String
  # using various heuristics; see #_parse in date/format.rb
  # for more details.  If parsing fails, an ArgumentError
  # will be raised.
  #
  # The default +str+ is '-4712-01-01'; this is Julian
  # Day Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.parse(str='-4712-01-01', comp=false, sg=ITALY)
    elem = _parse(str, comp)
    new_with_hash(elem, sg)
  end

  # Create a new Date object representing today.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.today(sg=ITALY)
    jd = civil_to_jd(*(Time.now.to_a[3..5].reverse << sg))
    new0(jd_to_ajd(jd, 0, 0), 0, sg)
  end

  class << self

    def once(*ids) # :nodoc:
      for id in ids
	module_eval <<-"end;", __FILE__, __LINE__
	  alias_method :__#{id.to_i}__, :#{id.to_s}
	  private :__#{id.to_i}__
	  def #{id.to_s}(*args, &block)
            if defined? @__#{id.to_i}__
              @__#{id.to_i}__
            elsif ! self.frozen?
	      @__#{id.to_i}__ ||= __#{id.to_i}__(*args, &block)
            else
               __#{id.to_i}__(*args, &block)
            end
	  end
	end;
      end
    end

    private :once

  end

  # *NOTE* this is the documentation for the method new0().  If
  # you are reading this as the documentation for new(), that is
  # because rdoc doesn't fully support the aliasing of the
  # initialize() method.
  # new() is in
  # fact an alias for #civil(): read the documentation for that
  # method instead.
  #
  # Create a new Date object.
  #
  # +ajd+ is the Astronomical Julian Day Number.
  # +of+ is the offset from UTC as a fraction of a day.
  # Both default to 0.
  #
  # +sg+ specifies the Day of Calendar Reform to use for this
  # Date object.
  #
  # Using one of the factory methods such as Date::civil is
  # generally easier and safer.
  def initialize(ajd=0, of=0, sg=ITALY) @ajd, @of, @sg = ajd, of, sg end

  # Get the date as an Astronomical Julian Day Number.
  def ajd() @ajd end

  # Get the date as an Astronomical Modified Julian Day Number.
  def amjd() self.class.ajd_to_amjd(@ajd) end

  once :amjd

  # Get the date as a Julian Day Number.
  def jd() self.class.ajd_to_jd(@ajd, @of)[0] end

  # Get any fractional day part of the date.
  def day_fraction() self.class.ajd_to_jd(@ajd, @of)[1] end

  # Get the date as a Modified Julian Day Number.
  def mjd() self.class.jd_to_mjd(jd) end

  # Get the date as the number of days since the Day of Calendar
  # Reform (in Italy and the Catholic countries).
  def ld() self.class.jd_to_ld(jd) end

  once :jd, :day_fraction, :mjd, :ld

  # Get the date as a Civil Date, [year, month, day_of_month]
  def civil() self.class.jd_to_civil(jd, @sg) end

  # Get the date as an Ordinal Date, [year, day_of_year]
  def ordinal() self.class.jd_to_ordinal(jd, @sg) end

  # Get the date as a Commercial Date, [year, week_of_year, day_of_week]
  def commercial() self.class.jd_to_commercial(jd, @sg) end

  once :civil, :ordinal, :commercial
  private :civil, :ordinal, :commercial

  # Get the year of this date.
  def year() civil[0] end

  # Get the day-of-the-year of this date.
  #
  # January 1 is day-of-the-year 1
  def yday() ordinal[1] end

  # Get the month of this date.
  #
  # January is month 1.
  def mon() civil[1] end

  # Get the day-of-the-month of this date.
  def mday() civil[2] end

  alias_method :month, :mon
  alias_method :day, :mday

  # Get the time of this date as [hours, minutes, seconds,
  # fraction_of_a_second]
  def time() self.class.day_fraction_to_time(day_fraction) end

  once :time
  private :time

  # Get the hour of this date.
  def hour() time[0] end

  # Get the minute of this date.
  def min() time[1] end

  # Get the second of this date.
  def sec() time[2] end

  # Get the fraction-of-a-second of this date.
  def sec_fraction() time[3] end

  private :hour, :min, :sec, :sec_fraction

  def zone() strftime('%Z') end

  private :zone

  # Get the commercial year of this date.  See *Commercial* *Date*
  # in the introduction for how this differs from the normal year.
  def cwyear() commercial[0] end

  # Get the commercial week of the year of this date.
  def cweek() commercial[1] end

  # Get the commercial day of the week of this date.  Monday is
  # commercial day-of-week 1; Sunday is commercial day-of-week 7.
  def cwday() commercial[2] end

  # Get the week day of this date.  Sunday is day-of-week 0;
  # Saturday is day-of-week 6.
  def wday() self.class.jd_to_wday(jd) end

  once :wday

  # Is the current date old-style (Julian Calendar)?
  def os? () self.class.os?(jd, @sg) end

  # Is the current date new-style (Gregorian Calendar)?
  def ns? () self.class.ns?(jd, @sg) end

  once :os?, :ns?

  # Is this a leap year?
  def leap?
    self.class.jd_to_civil(self.class.civil_to_jd(year, 3, 1, ns?) - 1,
      ns?)[-1] == 29
  end

  once :leap?

  # When is the Day of Calendar Reform for this Date object?
  def start() @sg end

  # Create a copy of this Date object using a new Day of Calendar Reform.
  def new_start(sg=self.class::ITALY) self.class.new0(@ajd, @of, sg) end

  # Create a copy of this Date object that uses the Italian/Catholic
  # Day of Calendar Reform.
  def italy() new_start(self.class::ITALY) end

  # Create a copy of this Date object that uses the English/Colonial
  # Day of Calendar Reform.
  def england() new_start(self.class::ENGLAND) end

  # Create a copy of this Date object that always uses the Julian
  # Calendar.
  def julian() new_start(self.class::JULIAN) end

  # Create a copy of this Date object that always uses the Gregorian
  # Calendar.
  def gregorian() new_start(self.class::GREGORIAN) end

  def offset() @of end
  def new_offset(of=0) self.class.new0(@ajd, of, @sg) end

  private :offset, :new_offset

  # Return a new Date object that is +n+ days later than the
  # current one.
  #
  # +n+ may be a negative value, in which case the new Date
  # is earlier than the current one; however, #-() might be
  # more intuitive.
  #
  # If +n+ is not a Numeric, a TypeError will be thrown.  In
  # particular, two Dates cannot be added to each other.
  def + (n)
    case n
    when Numeric; return self.class.new0(@ajd + n, @of, @sg)
    end
    raise TypeError, 'expected numeric'
  end

  # If +x+ is a Numeric value, create a new Date object that is
  # +x+ days earlier than the current one.
  #
  # If +x+ is a Date, return the number of days between the
  # two dates; or, more precisely, how many days later the current
  # date is than +x+.
  #
  # If +x+ is neither Numeric nor a Date, a TypeError is raised.
  def - (x)
    case x
    when Numeric; return self.class.new0(@ajd - x, @of, @sg)
    when Date;    return @ajd - x.ajd
    end
    raise TypeError, 'expected numeric or date'
  end

  # Compare this date with another date.
  #
  # +other+ can also be a Numeric value, in which case it is
  # interpreted as an Astronomical Julian Day Number.
  #
  # Comparison is by Astronomical Julian Day Number, including
  # fractional days.  This means that both the time and the
  # timezone offset are taken into account when comparing
  # two DateTime instances.  When comparing a DateTime instance
  # with a Date instance, the time of the latter will be
  # considered as falling on midnight UTC.
  def <=> (other)
    case other
    when Numeric; return @ajd <=> other
    when Date;    return @ajd <=> other.ajd
    end
    nil
  end

  # The relationship operator for Date.
  #
  # Compares dates by Julian Day Number.  When comparing
  # two DateTime instances, or a DateTime with a Date,
  # the instances will be regarded as equivalent if they
  # fall on the same date in local time.
  def === (other)
    case other
    when Numeric; return jd == other
    when Date;    return jd == other.jd
    end
    false
  end

  # Return a new Date object that is +n+ months later than
  # the current one.
  #
  # If the day-of-the-month of the current Date is greater
  # than the last day of the target month, the day-of-the-month
  # of the returned Date will be the last day of the target month.
  def >> (n)
    y, m = clfloor(year * 12 + (mon - 1) + n, 12)
    m,   = clfloor(m + 1, 1)
    d = mday
    d -= 1 until jd2 = self.class.valid_civil?(y, m, d, ns?)
    self + (jd2 - jd)
  end

  # Return a new Date object that is +n+ months earlier than
  # the current one.
  #
  # If the day-of-the-month of the current Date is greater
  # than the last day of the target month, the day-of-the-month
  # of the returned Date will be the last day of the target month.
  def << (n) self >> -n end

  # Step the current date forward +step+ days at a
  # time (or backward, if +step+ is negative) until
  # we reach +limit+ (inclusive), yielding the resultant
  # date at each step.
  def step(limit, step)  # :yield: date
    da = self
    op = [:-,:<=,:>=][step<=>0]
    while da.__send__(op, limit)
      yield da
      da += step
    end
    self
  end

  # Step forward one day at a time until we reach +max+
  # (inclusive), yielding each date as we go.
  def upto(max, &block)  # :yield: date
    step(max, +1, &block)
  end

  # Step backward one day at a time until we reach +min+
  # (inclusive), yielding each date as we go.
  def downto(min, &block) # :yield: date
    step(min, -1, &block)
  end

  # Return a new Date one day after this one.
  def succ() self + 1 end

  alias_method :next, :succ

  # Is this Date equal to +other+?
  #
  # +other+ must both be a Date object, and represent the same date.
  def eql? (other) Date === other and self == other end

  # Calculate a hash value for this date.
  def hash() @ajd.hash end

  # Return internal object state as a programmer-readable string.
  def inspect() format('#<%s: %s,%s,%s>', self.class, @ajd, @of, @sg) end

  # Return the date as a human-readable string.
  #
  # The format used is YYYY-MM-DD.
  def to_s() strftime end

  # Dump to Marshal format.
  def _dump(limit) Marshal.dump([@ajd, @of, @sg], -1) end

  # def self._load(str) new0(*Marshal.load(str)) end

  # Load from Marshall format.
  def self._load(str)
    a = Marshal.load(str)
    if a.size == 2
      ajd,     sg = a
      of = 0
      ajd -= 1.to_r/2
    else
      ajd, of, sg = a
    end
    new0(ajd, of, sg)
  end

end

# Class representing a date and time.
#
# See the documentation to the file date.rb for an overview.
#
# DateTime objects are immutable once created.
#
# == Other methods.
#
# The following methods are defined in Date, but declared private
# there.  They are made public in DateTime.  They are documented
# here.
#
# === hour()
#
# Get the hour-of-the-day of the time.  This is given
# using the 24-hour clock, counting from midnight.  The first
# hour after midnight is hour 0; the last hour of the day is
# hour 23.
#
# === min()
#
# Get the minute-of-the-hour of the time.
#
# === sec()
#
# Get the second-of-the-minute of the time.
#
# === sec_fraction()
#
# Get the fraction of a second of the time.  This is returned as
# a +Rational+.
#
# === zone()
#
# Get the time zone as a String.  This is representation of the
# time offset such as "+1000", not the true time-zone name.
#
# === offset()
#
# Get the time zone offset as a fraction of a day.  This is returned
# as a +Rational+.
#
# === new_offset(of=0)
#
# Create a new DateTime object, identical to the current one, except
# with a new time zone offset of +of+.  +of+ is the new offset from
# UTC as a fraction of a day.
#
class DateTime < Date

  # Do hour +h+, minute +min+, and second +s+ constitute a valid time?
  #
  # If they do, returns their value as a fraction of a day.  If not,
  # returns nil.
  #
  # The 24-hour clock is used.  Negative values of +h+, +min+, and
  # +sec+ are treating as counting backwards from the end of the
  # next larger unit (e.g. a +min+ of -2 is treated as 58).  No
  # wraparound is performed.
  def self.valid_time? (h, min, s)
    h   += 24 if h   < 0
    min += 60 if min < 0
    s   += 60 if s   < 0
    return unless (0..24) === h and
      (0..59) === min and
      (0..59) === s
    time_to_day_fraction(h, min, s)
  end

  # Create a new DateTime object corresponding to the specified
  # Julian Day Number +jd+ and hour +h+, minute +min+, second +s+.
  #
  # The 24-hour clock is used.  Negative values of +h+, +min+, and
  # +sec+ are treating as counting backwards from the end of the
  # next larger unit (e.g. a +min+ of -2 is treated as 58).  No
  # wraparound is performed.  If an invalid time portion is specified,
  # an ArgumentError is raised.
  #
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  # +sg+ specifies the Day of Calendar Reform.
  #
  # All day/time values default to 0.
  def self.jd(jd=0, h=0, min=0, s=0, of=0, sg=ITALY)
    unless (jd = valid_jd?(jd, sg)) and
        (fr = valid_time?(h, min, s))
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, fr, of), of, sg)
  end

  # Create a new DateTime object corresponding to the specified
  # Ordinal Date and hour +h+, minute +min+, second +s+.
  #
  # The 24-hour clock is used.  Negative values of +h+, +min+, and
  # +sec+ are treating as counting backwards from the end of the
  # next larger unit (e.g. a +min+ of -2 is treated as 58).  No
  # wraparound is performed.  If an invalid time portion is specified,
  # an ArgumentError is raised.
  #
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  # +sg+ specifies the Day of Calendar Reform.
  #
  # +y+ defaults to -4712, and +d+ to 1; this is Julian Day Number
  # day 0.  The time values default to 0.
  def self.ordinal(y=-4712, d=1, h=0, min=0, s=0, of=0, sg=ITALY)
    unless (jd = valid_ordinal?(y, d, sg)) and
        (fr = valid_time?(h, min, s))
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, fr, of), of, sg)
  end

  # Create a new DateTime object corresponding to the specified
  # Civil Date and hour +h+, minute +min+, second +s+.
  #
  # The 24-hour clock is used.  Negative values of +h+, +min+, and
  # +sec+ are treating as counting backwards from the end of the
  # next larger unit (e.g. a +min+ of -2 is treated as 58).  No
  # wraparound is performed.  If an invalid time portion is specified,
  # an ArgumentError is raised.
  #
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  # +sg+ specifies the Day of Calendar Reform.
  #
  # +y+ defaults to -4712, +m+ to 1, and +d+ to 1; this is Julian Day
  # Number day 0.  The time values default to 0.
  def self.civil(y=-4712, m=1, d=1, h=0, min=0, s=0, of=0, sg=ITALY)
    unless (jd = valid_civil?(y, m, d, sg)) and
        (fr = valid_time?(h, min, s))
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, fr, of), of, sg)
  end

  class << self; alias_method :new, :civil end

  # Create a new DateTime object corresponding to the specified
  # Commercial Date and hour +h+, minute +min+, second +s+.
  #
  # The 24-hour clock is used.  Negative values of +h+, +min+, and
  # +sec+ are treating as counting backwards from the end of the
  # next larger unit (e.g. a +min+ of -2 is treated as 58).  No
  # wraparound is performed.  If an invalid time portion is specified,
  # an ArgumentError is raised.
  #
  # +of+ is the offset from UTC as a fraction of a day (defaults to 0).
  # +sg+ specifies the Day of Calendar Reform.
  #
  # +y+ defaults to 1582, +w+ to 41, and +d+ to 5; this is the Day of
  # Calendar Reform for Italy and the Catholic countries.
  # The time values default to 0.
  def self.commercial(y=1582, w=41, d=5, h=0, min=0, s=0, of=0, sg=ITALY)
    unless (jd = valid_commercial?(y, w, d, sg)) and
        (fr = valid_time?(h, min, s))
      raise ArgumentError, 'invalid date'
    end
    new0(jd_to_ajd(jd, fr, of), of, sg)
  end

  def self.new_with_hash(elem, sg)
    elem ||= {}
    y, m, d, h, min, s, fr, of =
      elem.values_at(:year, :mon, :mday,
      :hour, :min, :sec, :sec_fraction, :offset)
    h   ||= 0
    min ||= 0
    s   ||= 0
    fr  ||= 0
    of  ||= 0
    if [y, m, d].include? nil
      raise ArgumentError, '3 elements of civil date are necessary'
    else
      civil(y, m, d, h, min, s, of.to_r/86400, sg) + (fr/86400)
    end
  end

  private_class_method :new_with_hash

  # Create a new DateTime object by parsing from a String
  # according to a specified format.
  #
  # +str+ is a String holding a date-time representation.
  # +fmt+ is the format that the date-time is in.  See
  # date/format.rb for details on supported formats.
  #
  # The default +str+ is '-4712-01-01T00:00:00Z', and the default
  # +fmt+ is '%FT%T%Z'.  This gives midnight on Julian Day Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  #
  # An ArgumentError will be raised if +str+ cannot be
  # parsed.
  def self.strptime(str='-4712-01-01T00:00:00Z', fmt='%FT%T%Z', sg=ITALY)
    elem = _strptime(str, fmt)
    new_with_hash(elem, sg)
  end

  # Create a new DateTime object by parsing from a String,
  # without specifying the format.
  #
  # +str+ is a String holding a date-time representation. 
  # +comp+ specifies whether to interpret 2-digit years
  # as 19XX (>= 69) or 20XX (< 69); the default is not to.
  # The method will attempt to parse a date-time from the String
  # using various heuristics; see #_parse in date/format.rb
  # for more details.  If parsing fails, an ArgumentError
  # will be raised.
  #
  # The default +str+ is '-4712-01-01T00:00:00Z'; this is Julian
  # Day Number day 0.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.parse(str='-4712-01-01T00:00:00Z', comp=false, sg=ITALY)
    elem = _parse(str, comp)
    new_with_hash(elem, sg)
  end

  class << self; undef_method :today end rescue nil

  # Create a new DateTime object representing the current time.
  #
  # +sg+ specifies the Day of Calendar Reform.
  def self.now(sg=ITALY)
    i = Time.now
    a = i.to_a[0..5].reverse
    jd = civil_to_jd(*(a[0,3] << sg))
    fr = time_to_day_fraction(*(a[3,3])) + i.usec.to_r/86400000000
    of = i.utc_offset.to_r/86400
    new0(jd_to_ajd(jd, fr, of), of, sg)
  end

  public :hour, :min, :sec, :sec_fraction, :zone, :offset, :new_offset

end

class Date

  [ %w(exist1?	valid_jd?),
    %w(exist2?	valid_ordinal?),
    %w(exist3?	valid_date?),
    %w(exist?	valid_date?),
    %w(existw?	valid_commercial?),
    %w(new1	jd),
    %w(new2	ordinal),
    %w(new3	new),
    %w(neww	commercial)
  ].each do |old, new|
    module_eval <<-"end;"
      def self.#{old}(*args, &block)
	if $VERBOSE
	  warn("\#{caller.shift.sub(/:in .*/, '')}: " \
	       "warning: \#{self}::#{old} is deprecated; " \
	       "use \#{self}::#{new}")
	end
	#{new}(*args, &block)
      end
    end;
  end

  [ %w(sg	start),
    %w(newsg	new_start),
    %w(of	offset),
    %w(newof	new_offset)
  ].each do |old, new|
    module_eval <<-"end;"
      def #{old}(*args, &block)
	if $VERBOSE
	  warn("\#{caller.shift.sub(/:in .*/, '')}: " \
	       "warning: \#{self.class}\##{old} is deprecated; " \
	       "use \#{self.class}\##{new}")
	end
	#{new}(*args, &block)
      end
    end;
  end

  private :of, :newof

end

class DateTime < Date

  public :of, :newof

end
