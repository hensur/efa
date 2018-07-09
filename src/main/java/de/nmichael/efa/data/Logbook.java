/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.Daten;
import de.nmichael.efa.ex.EfaException;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.ex.EfaModifyException;

import java.util.*;

// @i18n complete

public class Logbook extends StorageObject {

    public static final String DATATYPE = "efa2logbook";

    private String name;
    private ProjectRecord projectRecord;

    public Logbook(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, 
                International.getString("Fahrtenbuch") + " " + storageObjectName);
        LogbookRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
    }

    /**
     * Save or update a LogbookRecord in the logbook.
     *
     * @param r The record to save/update
     * @param isNewRecord true if the record should be treated as new, false if it is an update
     * @param oldRecord in case of an update, give the oldRecord for comparison
     * @return The new, updated, record or the previously given one if it was a new record
     * @throws EfaException thrown if the record can't be added
     */
    public LogbookRecord save(LogbookRecord r, boolean isNewRecord, LogbookRecord oldRecord) throws EfaException {
        if (!isOpen()) {
            return null;
        }

        long lock = 0;
        try {
            boolean changeEntryNo = false;
            if (!isNewRecord && oldRecord != null && !oldRecord.getEntryId().equals(r.getEntryId())) {
                // the entry number has probably changed, we will therefore delete the old record
                lock = data().acquireGlobalLock();
                data().delete(oldRecord.getKey(), lock);
                changeEntryNo = true;
            }

            if (isNewRecord || changeEntryNo) {
                data().add(r, lock);
                return r;
            } else {
                DataRecord newRecord = data().update(r, lock);
                return (LogbookRecord) newRecord;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (lock != 0) {
                data().releaseGlobalLock(lock);
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProjectRecord(ProjectRecord r) {
        this.projectRecord = r;
    }

    public DataTypeDate getStartDate() {
        return (projectRecord != null ? projectRecord.getStartDate() : null);
    }

    public DataTypeDate getEndDate() {
        return (projectRecord != null ? projectRecord.getEndDate() : null);
    }

    public long getValidFrom() {
        DataTypeDate date = getStartDate();
        if (date != null && date.isSet()) {
            return date.getTimestamp(null);
        }
        return 0;
    }

    public long getInvalidFrom() {
        DataTypeDate date = getEndDate();
        if (date != null && date.isSet()) {
            long validFrom = getValidFrom();
            long invalidFrom = date.getTimestamp(null) + 24 * 60 * 60 * 1000;
            if (invalidFrom < validFrom) {
                invalidFrom = validFrom + 24 * 60 * 60 * 1000;
            }
            return invalidFrom;
        }
        return Long.MAX_VALUE;
    }

    public LogbookRecord createLogbookRecord(DataTypeIntString entryNo) {
        LogbookRecord r = new LogbookRecord(this, MetaData.getMetaData(DATATYPE));
        r.setEntryId(entryNo);
        return r;
    }

    public LogbookRecord getLogbookRecord(DataTypeIntString entryNo) {
        return getLogbookRecord(LogbookRecord.getKey(entryNo));
    }

    public LogbookRecord getLogbookRecord(DataKey key) {
        try {
            return (LogbookRecord)(data().get(key));
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }

    public LogbookRecord findDuplicateEntry(LogbookRecord r, int rangeFromEnd) {
        if (r == null || r.getEntryId() == null ||
            r.getDate() == null || !r.getDate().isSet() ||
            r.getBoatAsName().length() == 0) {
            return null;
        }
        Vector<String> p = r.getAllCoxAndCrewAsNames();
        try {
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getLast();
            while (k != null && rangeFromEnd-- > 0) {
                LogbookRecord r0 = getLogbookRecord(k);
                if (r0 != null &&
                    !r.getEntryId().equals(r0.getEntryId()) &&
                    r.getDate().equals(r0.getDate()) &&
                    r.getBoatAsName().equals(r0.getBoatAsName())) {
                    // Records are identical in Data and BoatName
                    Vector<String> p0 = r0.getAllCoxAndCrewAsNames();
                    int matches = 0;
                    for (int i=0; i<p0.size(); i++) {
                        if (p.contains(p0.get(i))) {
                            matches++;
                        }
                    }
                    if (matches > 0 && p.size() - matches <=2) {
                        // at least one crew member identical, and less than 2 crew members different
                        // --> this is a potentially duplicate record
                        return r0;
                    }
                }
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public LogbookRecord getLastBoatUsage(UUID boatId, LogbookRecord notThisRecord) {
        try {
            LogbookRecord latest = null;
            DataKeyIterator it = data().getStaticIterator();
            for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                LogbookRecord r = (LogbookRecord) data().get(k);
                if (r == null || r.getBoatId() == null || !r.getBoatId().equals(boatId)) {
                    continue;
                }
                if (notThisRecord != null && notThisRecord.getEntryId() != null &&
                    r.getEntryId() != null && r.getEntryId().equals(notThisRecord.getEntryId())) {
                    continue;
                }
                if (r.getDate() != null && r.getDate().isSet()) {
                    if (latest == null
                            || r.getDate().isAfter(latest.getDate())
                            || (r.getDate().equals(latest.getDate())
                            && r.getStartTime() != null
                            && r.getStartTime().isSet()
                            && (latest.getStartTime() == null || r.getStartTime().isAfter(latest.getStartTime())))) {
                        latest = r;
                    }
                }
            }
            return latest;
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }
    
    public DataTypeIntString getNextEntryNo() {
        int n = 1;
        LogbookRecord lastrec = null;
        try {
            lastrec = (LogbookRecord) data().getLast();
        } catch (Exception e) {
            Logger.logdebug(e);
        }
        if (lastrec != null && lastrec.getEntryId() != null) {
            n = EfaUtil.stringFindInt(lastrec.getEntryId().toString(), 0) + 1;
        } else {
            n = 1;
        }
        return new DataTypeIntString(Integer.toString(n));
    }

    public boolean isDateWithinLogbookRange(DataTypeDate d) {
        if (d == null || !d.isSet()) {
            return true;
        }
        DataTypeDate startDate = getStartDate();
        if (startDate == null || !startDate.isSet()) {
            return true; // should only happen in case of remote access not possible
        }
        DataTypeDate endDate = getEndDate();
        if (endDate == null || !endDate.isSet()) {
            return true; // should only happen in case of remote access not possible
        }
        return d.isAfterOrEqual(startDate) && d.isBeforeOrEqual(endDate);
    }

    /**
     * Save a Record to tmp dir and create a late entry message for the admin.
     * The admin can then import the tmp record and save it to the logbook.
     *
     * @return MessageRecord that can be sent to the admin
     */
    public MessageRecord saveForLateEntry(LogbookRecord r) {
        String fName = Daten.efaTmpDirectory + "entry_" +
                EfaUtil.getCurrentTimeStampYYYYMMDD_HHMMSS() + ".xml";
        r.setEntryId(null);
        if (r.saveRecordToXmlFile(fName)) {
            MessageRecord tmp = Daten.project.getMessages(false).createMessageRecord();
            tmp.setTo(MessageRecord.TO_ADMIN);
            tmp.setFrom(Daten.EFA_SHORTNAME);
            tmp.setSubject(International.getString("Nachtrag"));
            tmp.setText(
                    International.getMessage("Ein Nachtrag für {datum} konnte im Fahrtenbuch {logbook} nicht gespeichert werden, " +
                                    "da sein Datum außerhalb des Zeitraums für dieses Fahrtenbuch liegt ({dateFrom} - {dateTo}).",
                            r.getDate().toString(), getName(),
                            getStartDate().toString(), getEndDate().toString()) + "\n\n" +
                            r.getLogbookRecordAsStringDescription() + "\n\n" +
                            International.getMessage("Der Eintrag wurde als Importdatei {name} abgespeichert " +
                                            "und kann durch Import dieser Datei zum entsprechenden Fahrtenbuch hinzugefügt werden.",
                                    fName)
            );
            return tmp;
        }
        return null;
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, LogbookRecord.ENTRYID);
            assertUnique(record, LogbookRecord.ENTRYID);
            
            LogbookRecord r = (LogbookRecord)record;

            // make sure both start and end date are within logbook range
            if (!isDateWithinLogbookRange(r.getDate())) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        "#" + r.getEntryId().toString() + ": " +
                        International.getMessage("Datum {date} muß innerhalb des Zeitraums {startdate} - {enddate} liegen.",
                        r.getDate().toString(), getStartDate().toString(), getEndDate().toString()),
                        Thread.currentThread().getStackTrace());
            }
            if (!isDateWithinLogbookRange(r.getEndDate())) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        "#" + r.getEntryId().toString() + ": " +
                        International.getMessage("Datum {date} muß innerhalb des Zeitraums {startdate} - {enddate} liegen.",
                        r.getDate().toString(), getStartDate().toString(), getEndDate().toString()),
                        Thread.currentThread().getStackTrace());
            }

            // make sure enddate is after startdate
            if (r.getDate() != null && r.getDate().isSet() && r.getEndDate() != null && r.getEndDate().isSet() && !r.getDate().isBefore(r.getEndDate())) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        "#" + r.getEntryId().toString() + ": " +
                        International.getString("Das Enddatum muß nach dem Startdatum liegen."),
                        Thread.currentThread().getStackTrace());
            }

            // make sure that the entry's date fits into the selected session group
            SessionGroupRecord sg = r.getSessionGroup();
            if (sg != null && !sg.checkLogbookRecordFitsIntoRange(r)) {
                    throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                              International.getMessage("Das Datum des Fahrtenbucheintrags {entry} liegt außerhalb des Zeitraums, "
                        + "der für die ausgewählte Fahrtgruppe '{name}' angegeben wurde.",
                        r.getEntryId().toString(), sg.getName()),
                    Thread.currentThread().getStackTrace());
            }

            // select boat variant, if empty
            if (r.getBoatVariant() == IDataAccess.UNDEFINED_INT &&
                r.getBoatId() != null) {
                try {
                    BoatRecord br = r.getPersistence().getProject().getBoats(false).getBoat(r.getBoatId(), r.getValidAtTimestamp());
                    if (br != null) {
                        r.setBoatVariant(br.getTypeVariant(0));
                    }
                } catch(Exception eignore) {
                    Logger.logdebug(eignore);
                }
            }
        }
        if (delete) {
            assertNotReferenced(record, getProject().getBoatStatus(false), new String[] { BoatStatusRecord.ENTRYNO }, true,
                    new String[] { BoatStatusRecord.LOGBOOK }, new String[] { getName() } );
        }
    }

}
