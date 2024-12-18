package app.barun.journalApp.service;

import app.barun.journalApp.entity.Journal;
import app.barun.journalApp.entity.User;
import app.barun.journalApp.repository.JournalRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(Journal journal, String userName){
        try {
            User user = userService.findByUserName(userName);
            journal.setDate(LocalDateTime.now());
            Journal saved = journalRepository.save(journal);
            user.getJournal().add(saved);
            userService.saveUser(user);
        } catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("An error occurred while saving the entry.", e);
        }
    }

    public void saveEntry(Journal journal){
        journalRepository.save(journal);
    }

    public List<Journal> getAll(){
        return journalRepository.findAll();
    }

    public Optional<Journal> findById(ObjectId id){
        return journalRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName){
        boolean removed = false;
        try{
            User user = userService.findByUserName(userName);
            removed = user.getJournal().removeIf(x -> x.getId().equals(id));
            if(removed){
                userService.saveUser(user);
                journalRepository.deleteById(id);
            }
        } catch (Exception e){
            log.error("Error");
            throw new RuntimeException("An error occurred while deleting the entry.", e);
        }

        return removed;
    }
}
