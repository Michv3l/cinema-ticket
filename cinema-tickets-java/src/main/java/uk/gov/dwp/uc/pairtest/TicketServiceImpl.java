package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        TicketPaymentServiceImpl ticketPayment = new TicketPaymentServiceImpl(); //initialize ticket payment service
        SeatReservationServiceImpl seatReserrve = new SeatReservationServiceImpl(); //initialize seat reservation service
              
        int amountOfSeats = 0; // number of allocated seats
        int amountofTickets = 0; //total number of tickets in the ticket type request
        int cumulativePrice = 0; // total price of tickets purchase
        boolean adultPresent = false; // boolean value to confirm an adult ticket is present in the purchase
        boolean childPresent = false; // boolean value to confirm a child or infant ticket is present in the purchase
        
        //ensures there are ticket requests
        if (ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException();
        }

        /*
         * loop to iterate through each ticket type request passed into method
         */
        for(int i = 0; i < ticketTypeRequests.length; i++){

            TicketTypeRequest ticket = ticketTypeRequests[i];
            int noOftickets = ticket.getNoOfTickets();
            if (noOftickets == 0){//if block for checking type requests 0 tickets

            }else{
                switch(ticket.getTicketType()){
                    case INFANT:
                    childPresent = true;
                    amountofTickets += noOftickets;
                    break;
                    case CHILD:
                    childPresent = true;
                    cumulativePrice += (noOftickets * 10);
                    amountOfSeats += noOftickets;
                    amountofTickets += noOftickets;
                    break;
                    case ADULT:
                    adultPresent = true;
                    cumulativePrice += (noOftickets * 20);
                    amountOfSeats += noOftickets;
                    amountofTickets += noOftickets;
                    break;
                    default:
                    throw new InvalidPurchaseException();
                }
            }
            
        }
        /*
         * If statements to ensure account ID is greater than 1, the number of tickets is not 0, and the amount of tickets in request is less
         * than 20
         */
        if((accountId<1)||(amountofTickets==0)||(amountofTickets>20)){
            throw new InvalidPurchaseException();
        }
        if(childPresent == true && adultPresent != true){
            throw new InvalidPurchaseException();
        }

        ticketPayment.makePayment(accountId, cumulativePrice);// makes payment request to ticket payment service
        seatReserrve.reserveSeat(accountId, amountOfSeats);// makes reservation request to seat reservation service
        
    }

}
