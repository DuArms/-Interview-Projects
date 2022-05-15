package Project.offer.pendingrequest;

import Project.appuser.AppUser;
import Project.appuser.AppUserRepository;
import Project.email.EmailSender;
import Project.email.EmailService;
import Project.offer.Offer;
import Project.offer.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PendingOfferService {

    @Autowired
    private PendingOfferRepository pendingOfferRepositor;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private EmailService emailSender;

    public List<PendingOffer> getPendingOffersByOffer(Long offerID) {

        var pendingOffers = pendingOfferRepositor.findAllByOffer(offerRepository.findById(offerID).get());


        return pendingOffers;
    }

    public boolean save(Long userId, Long offerId, String description) {

        PendingOffer pendingOffer = new PendingOffer();

        Optional<Offer> offer = offerRepository.findById(offerId);
        Optional<AppUser> appUser = appUserRepository.findById(userId);

        if (!(offer.isPresent() && appUser.isPresent())) {
            return false;
        }
        pendingOffer.setOffer(offer.get());
        pendingOffer.setReguestedbyuser(appUser.get());
        pendingOffer.setCreatedAt(LocalDateTime.now());
        pendingOffer.setAccepted(false);
        pendingOffer.setDescription(description);

        pendingOfferRepositor.save(pendingOffer);
        //emailSender.notifyNewPendingRequest(offer.get().getAppUser().getEmail(), buildEmail(offer.get().getAppUser().getFirstName()));
        return true;
    }

    public boolean confirm(Long id) {
        // naive accept
        PendingOffer pendingOffer = pendingOfferRepositor.findById(id).get();
        pendingOffer.setAccepted(true);
        pendingOffer.setConfirmedAt(LocalDateTime.now());

        pendingOfferRepositor.save(pendingOffer);
        return true;
    }


    private String buildEmail(String userName) {
        return "<div>\n" +
                "\n" +
                "        <h2> Hello " + userName + "!</h2>\n" +
                "\n" +
                "\n" +
                "        <p>\n" +
                "            Some one is interested in your offer!\n" +
                "\n" +
                "            Thank you for your help!\n" +
                "\n" +
                "            All the best for Ukraina!\n" +
                "        </p>\n" +
                "    </div>";
    }

}
