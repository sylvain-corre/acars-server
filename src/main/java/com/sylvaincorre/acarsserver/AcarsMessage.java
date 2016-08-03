package com.sylvaincorre.acarsserver;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Acars message.
 *
 * Created by Sylvain Corré on 28/07/16.
 */
public class AcarsMessage {
    /**
     * Date/Time formatter.
     */
    private final SimpleDateFormat dateFormat;

    /**
     * Date and time of reception of the message.
     */
    private final Date dateTime;

    /**
     * Channel on which the message has been received.
     */
    private final int channel;

    /**
     * Number of errors in the message while decoding.
     */
    private final int err;

    /**
     * Power level of the received message.
     */
    private final int level;

    /**
     * ACARS mode of the message.
     */
    private final char mode;

    /**
     * Aircraft registration.
     */
    private final String registration;

    /**
     * ACK flag.
     */
    private final char ack;

    /**
     * Message label.
     */
    private final String label;

    /**
     * Block ID of the message.
     */
    private final char blockId;

    /**
     * Message ID.
     */
    private final String msgId;

    /**
     * Flight ID.
     */
    private final String flightId;

    /**
     * Content of the message.
     */
    private final String text;

    /**
     * Frequency on which the message has been received.
     */
    private String frequency = null;

    /**
     * Constructor.
     *
     * @param rawMessage the raw message as received from acarsdec.
     */
    public AcarsMessage(final String rawMessage) {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        dateTime = dateFormat.parse(rawMessage, new ParsePosition(11));
        channel = Integer.parseInt(rawMessage.substring(9, 10));
        err = Integer.parseInt(rawMessage.substring(31, 32));
        level = Integer.parseInt(rawMessage.substring(33, 36));
        mode = rawMessage.charAt(37);
        registration = parseRegistration(rawMessage.substring(39, 46));
        ack = rawMessage.charAt(47);
        label = rawMessage.substring(49, 51);
        blockId = rawMessage.charAt(52);
        msgId = rawMessage.substring(54, 58);
        flightId = rawMessage.substring(59, 65);
        text = (rawMessage.length() > 67 ? rawMessage.substring(67) : "");
    }

    /**
     * Removes the extra '.' characters (padding).
     *
     * @param reg the registration received
     * @return the actual aircraft registration
     */
    private static String parseRegistration(String reg) {
        for (int i = 0; i < reg.length(); i++)
            if (reg.charAt(i) != '.')
                return reg.substring(i);

        return "";
    }

    /**
     * Getter for the date and time of the message.
     *
     * @return the date and time of the message
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Getter for the channel on which the message has been received.
     *
     * @return the channel on which the message has been received
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Getter for the number or errors found while decoding the message.
     *
     * @return the number or errors found while decoding the message
     */
    public int getErr() {
        return err;
    }

    /**
     * Getter for the power level of the received message.
     *
     * @return the power level of the received message
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter for the message mode.
     *
     * @return the message mode
     */
    public char getMode() {
        return mode;
    }

    /**
     * Getter for the aircraft registration.
     *
     * @return the aircraft registration
     */
    public String getRegistration() {
        return registration;
    }

    /**
     * Getter for the ACK flag.
     *
     * @return the ACK flag.
     */
    public char getAck() {
        return ack;
    }

    /**
     * Getter for the message label.
     *
     * @return the message label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Getter for the block ID.
     *
     * @return the block ID
     */
    public char getBlockId() {
        return blockId;
    }

    /**
     * Getter for the message ID.
     *
     * @return the message ID
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Getter for the flight ID.
     *
     * @return the flight ID.
     */
    public String getFlightId() {
        return flightId;
    }

    /**
     * Getter for the message content.
     *
     * @return the message content
     */
    public String getText() {
        return text;
    }

    /**
     * Getter for the frequency.
     *
     * @return the frequency on which the message has been received
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Setter for the frequency.
     *
     * @param frequency the frequency on which the message has been received
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "-------------------------------------------------------------------\n"
                + dateFormat.format(dateTime) + "    channel: " + channel
                + (frequency != null ? "   frequency: " + frequency + " MHz" : "")
                + "   err: " + err + "   level: " + level + "\n"
                + "Registration: " + registration + "   FlightID: " + flightId + "\n"
                + "Mode: " + mode + "   label: " + label + "   ack: " + ack + "   BlockID: " + blockId
                + "   MsgID: " + msgId + "\n" + "Message:\n" + text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcarsMessage that = (AcarsMessage) o;

        if (dateTime.after(that.dateTime)) {
            if (dateTime.getTime() - that.dateTime.getTime() > 3600000)
                return false;
        } else {
            if (that.dateTime.getTime() - dateTime.getTime() > 3600000)
                return false;
        }

        if (channel != that.channel) return false;
        if (mode != that.mode) return false;
        if (blockId != that.blockId) return false;
        if (!registration.equals(that.registration)) return false;
        if (!label.equals(that.label)) return false;
        if (!msgId.equals(that.msgId)) return false;
        return flightId.equals(that.flightId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = channel;
        result = 31 * result + (int) mode;
        result = 31 * result + registration.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + (int) blockId;
        result = 31 * result + msgId.hashCode();
        result = 31 * result + flightId.hashCode();
        return result;
    }
}
