import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;

public class SkiCenterSearchFrame extends JFrame {

    private final JTextField searchField;
    private final JTable resultsTable;
    private final JComboBox<String> searchTypeComboBox;
    private final int currentUserId;

    public SkiCenterSearchFrame(int currentUserId) {
        this.currentUserId = currentUserId;

        setTitle("Search Ski Centers");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting a color scheme
        Color backgroundColor = new Color(225, 245, 254); // Light blue
        Color buttonColor = new Color(3, 169, 244); // Blue
        Color textColor = Color.WHITE;

        // Setting background color
        getContentPane().setBackground(backgroundColor);

        // Welcome message
        JLabel welcomeMessage = new JLabel("Find the right ski center for you. We're here to help!", SwingConstants.CENTER);
        welcomeMessage.setFont(new Font("Serif", Font.BOLD, 18));
        welcomeMessage.setOpaque(true);
        welcomeMessage.setBackground(new Color(135, 206, 250)); // Light blue background
        welcomeMessage.setForeground(Color.WHITE); // White text
        welcomeMessage.setPreferredSize(new Dimension(this.getWidth(), 40)); // Set preferred size

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(backgroundColor); // Set background color
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel searchLabel = new JLabel("Search by:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(buttonColor);
        searchButton.setForeground(textColor);

        // Initialize the search type combo box with options
        String[] searchTypes = {"Name", "City", "County", "Region", "All"};
        searchTypeComboBox = new JComboBox<>(searchTypes);

        searchPanel.add(searchLabel);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Combine welcome message and search panel into a single panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(welcomeMessage, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);

        // Add the combined panel to the frame
        add(northPanel, BorderLayout.NORTH);

        // Results table setup
        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);

        // 'My Account' Button
        JButton myAccountButton = new JButton("My Account");
        myAccountButton.addActionListener(e -> openMyAccount(currentUserId));

        // Center the 'My Account' button in the south region
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        southPanel.add(myAccountButton);
        add(southPanel, BorderLayout.SOUTH);

        // Event listeners
        searchButton.addActionListener(e -> performSearch());
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row != -1) {
                    String name = (String) table.getValueAt(row, 0);
                    displaySkiCenterInfo(name);
                }
            }
        });
    }

    private void openMyAccount(int userId) {
        JFrame accountFrame = new JFrame("My Account");
        accountFrame.setSize(600, 400);
        accountFrame.setLayout(new BorderLayout());
        accountFrame.setLocationRelativeTo(null); // Center on screen

        ReviewDAO reviewDAO = new ReviewDAO();

        String username = reviewDAO.getUsernameById(userId); // Retrieve the username
        JLabel userLabel = new JLabel("You are connected as: " + username + ". Here are the reviews you have submitted for us.");
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        accountFrame.add(userLabel, BorderLayout.NORTH);

        JButton deleteAccountButton = new JButton("Delete My Account");
        deleteAccountButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    accountFrame,
                    "Are you sure you want to delete your account? This will delete all your reviews and cannot be undone.",
                    "Confirm Account Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean reviewsDeleted = reviewDAO.deleteAllReviewsByUserId(userId);
                if (reviewsDeleted && reviewDAO.deleteUserAccount(userId)) {
                    JOptionPane.showMessageDialog(accountFrame, "Account deleted successfully.");
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(accountFrame, "Failed to delete the account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(userLabel, BorderLayout.NORTH);

        deleteAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonPanel = new JPanel(); // A panel for the delete button
        buttonPanel.add(deleteAccountButton);

        JButton changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.addActionListener(e -> {
            String newUsername = JOptionPane.showInputDialog(
                    accountFrame,
                    "Enter your new username:",
                    "Change Username",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (newUsername != null && !newUsername.trim().isEmpty()) {
                boolean success = reviewDAO.changeUsername(userId, newUsername.trim());
                if (success) {
                    JOptionPane.showMessageDialog(accountFrame, "Username updated successfully.");
                    accountFrame.dispose();
                    openMyAccount(userId); // Refresh the account view
                } else {
                    JOptionPane.showMessageDialog(accountFrame, "Failed to update the username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(changeUsernameButton);

        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        accountFrame.add(northPanel, BorderLayout.NORTH);

        JPanel reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<Review, String> userReviews = reviewDAO.getReviewsByUserId(userId);

        for (Map.Entry<Review, String> entry : userReviews.entrySet()) {
            Review review = entry.getKey();
            String reviewName = entry.getValue() != null ? entry.getValue() : "Review";

            JPanel reviewPanel = new JPanel();
            reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
            reviewPanel.setBorder(BorderFactory.createTitledBorder(reviewName));

            String starRating = convertRatingToStars(review.getRating());
            JLabel ratingLabel = new JLabel("Overall Rating: " + starRating);
            Font starFont = new Font("SansSerif", Font.PLAIN, 20);
            ratingLabel.setFont(starFont);
            reviewPanel.add(ratingLabel);

            addSpecificRatings(reviewPanel, review);

            JTextArea reviewText = new JTextArea(review.getReviewText());
            reviewText.setFont(new Font("SansSerif", Font.PLAIN, 12));
            reviewText.setWrapStyleWord(true);
            reviewText.setLineWrap(true);
            reviewText.setEditable(false);
            reviewText.setBackground(new Color(233, 236, 239));
            reviewPanel.add(reviewText);

            JButton deleteButton = new JButton("Delete Review");
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        accountFrame,
                        "Are you sure you want to delete this review?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = reviewDAO.deleteReviewById(review.getReviewId());
                    if (success) {
                        JOptionPane.showMessageDialog(accountFrame, "Review deleted successfully.");
                        accountFrame.dispose();
                        openMyAccount(userId); // Refresh the account view
                    } else {
                        JOptionPane.showMessageDialog(accountFrame, "Failed to delete the review.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            reviewPanel.add(deleteButton);
            reviewsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between reviews
            reviewsPanel.add(reviewPanel);
        }

        JScrollPane scrollPane = new JScrollPane(reviewsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        accountFrame.add(scrollPane, BorderLayout.CENTER);
        accountFrame.setVisible(true);
    }

    private void performSearch() {
        String searchText = searchField.getText();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        SkiCenterDAO dao = new SkiCenterDAO();

        if ("All".equalsIgnoreCase(searchType)) {
            displayAllSkiCenters();
            return;
        }

        assert searchType != null;
        List<SkiCenter> searchResults = dao.getSkiCentersByLocation(mapSearchTypeToColumn(searchType), searchText);

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{"Name"}, 0);
        for (SkiCenter skiCenter : searchResults) {
            model.addRow(new Object[]{skiCenter.getName()});
        }
        resultsTable.setModel(model);
    }

    private void displayAllSkiCenters() {
        SkiCenterDAO dao = new SkiCenterDAO();
        List<SkiCenter> allSkiCenters = dao.getAllSkiCenters(); // Retrieve all ski centers from the database

        if (allSkiCenters.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ski centers found.", "All Ski Centers", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{"Name"}, 0);
        for (SkiCenter skiCenter : allSkiCenters) {
            model.addRow(new Object[]{skiCenter.getName()});
        }
        resultsTable.setModel(model);
    }

    private void displaySkiCenterInfo(String name) {
        SkiCenterDAO skiCenterDAO = new SkiCenterDAO();
        List<SkiCenter> skiCenters = skiCenterDAO.getSkiCentersByName(name);

        if (!skiCenters.isEmpty()) {
            SkiCenter skiCenter = skiCenters.get(0);

            // Calculate the overall review rating
            double overallReview = calculateOverallReview(skiCenter.getCenterId(), ReviewType.SKI_CENTER);

            // Create the info frame for ski center details
            JFrame infoFrame = new JFrame(skiCenter.getName() + " - Ski Center Details");
            infoFrame.setSize(800, 600);
            infoFrame.setLayout(new BorderLayout());
            infoFrame.setLocationRelativeTo(null);

            // Create a scrollable panel for ski center details
            JPanel infoPanel = new JPanel();
            JScrollPane infoScrollPane = new JScrollPane(infoPanel);
            infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            infoFrame.add(infoScrollPane, BorderLayout.CENTER);

            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

            // Create a title label for the ski center name
            JLabel titleLabel = new JLabel(skiCenter.getName());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            infoPanel.add(titleLabel);
            infoPanel.add(Box.createVerticalStrut(10)); // Add vertical spacing

            // Add ski center information fields with improved visuals
            addInfoField(infoPanel, "City:", skiCenter.getCity());
            addInfoField(infoPanel, "County:", skiCenter.getCounty());
            addInfoField(infoPanel, "Region:", skiCenter.getRegion());
            addInfoField(infoPanel, "Phone Number:", skiCenter.getPhoneNumber());
            addInfoField(infoPanel, "Email:", skiCenter.getEmail());
            addInfoField(infoPanel, "Operating Time:", skiCenter.getOperatingTime());

            // Display the overall review rating
            JLabel overallReviewLabel = new JLabel("Overall Review: " + String.format("%.2f", overallReview));
            overallReviewLabel.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(overallReviewLabel);
            infoPanel.add(Box.createVerticalStrut(10)); // Add vertical spacing

            // Ski Slopes section
            SkiSlopeDAO skiSlopeDAO = new SkiSlopeDAO();
            List<SkiSlope> skiSlopes = skiSlopeDAO.getSkiSlopesByCenterId(skiCenter.getCenterId());
            JTextPane skiSlopesTextPane = createSkiSlopesTextPane(skiSlopes);
            JLabel skiSlopesLabel = new JLabel("Ski Slopes:");
            skiSlopesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            infoPanel.add(skiSlopesLabel);
            infoPanel.add(new JScrollPane(skiSlopesTextPane));
            infoPanel.add(Box.createVerticalStrut(10)); // Add vertical spacing

            // Ski Center Amenities section
            AmenityDAO amenityDAO = new AmenityDAO();
            List<Amenity> skiCenterAmenities = amenityDAO.getAmenitiesForSkiCenter(skiCenter.getCenterId());
            JTable skiCenterAmenitiesTable = createAmenitiesTable(skiCenterAmenities);
            JLabel amenitiesLabel = new JLabel("Ski Center Amenities:");
            amenitiesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            infoPanel.add(amenitiesLabel);
            infoPanel.add(new JScrollPane(skiCenterAmenitiesTable));
            infoPanel.add(Box.createVerticalStrut(10)); // Add vertical spacing

            // Accommodation section
            JLabel accommodationsLabel = new JLabel("Accommodations:");
            accommodationsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            infoPanel.add(accommodationsLabel);
            String[] distanceOptions = {"All distances", "< 1000m", "< 5000m", "< 10000m"};
            JComboBox<String> distanceFilterComboBox = new JComboBox<>(distanceOptions);
            infoPanel.add(new JLabel("Filter by distance:"));
            infoPanel.add(distanceFilterComboBox);
            JScrollPane accommodationsScrollPane = new JScrollPane();
            infoPanel.add(accommodationsScrollPane);

            // Function to update accommodations table
            Consumer<String> updateAccommodationsTable = (filter) -> {
                JTable accommodationsTable = createAccommodationsTable(skiCenter.getCenterId(), filter);
                accommodationsScrollPane.setViewportView(accommodationsTable);
            };

            // Initialize accommodations table with default filter
            updateAccommodationsTable.accept("All distances");

            // Listener for combo box
            distanceFilterComboBox.addActionListener(e -> {
                String selectedFilter = (String) distanceFilterComboBox.getSelectedItem();
                updateAccommodationsTable.accept(selectedFilter);
            });

            // Finalize the frame
            infoFrame.setVisible(true);

            JButton reviewButton = new JButton("See Reviews");
            reviewButton.addActionListener(e -> openReviewsFrame(skiCenter.getCenterId(), ReviewType.SKI_CENTER));
            infoPanel.add(reviewButton);
        }
    }

    private double calculateOverallReview(int centerId, ReviewType reviewType) {
        ReviewDAO reviewDAO = new ReviewDAO();
        List<Review> reviews = reviewDAO.getReviewsByEntityId(reviewType, centerId);

        if (reviews.isEmpty()) {
            // If there are no reviews, return a default rating (e.g., 3)
            return 5.0;
        }

        double totalRating = 0.0;
        int reviewCount = 0;

        for (Review review : reviews) {
            // Get the overall rating from each review
            double overallRating = review.getRating();

            totalRating += overallRating;
            reviewCount++;
        }

        // Calculate the overall average rating based on the overall ratings from all reviews
        double overallAverageRating = totalRating / reviewCount;

        return overallAverageRating;
    }

    private JTable createAmenitiesTable(List<Amenity> amenities) {
        String[] columnNames = {"Amenity Name", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Amenity amenity : amenities) {
            String price = amenity.isFree() ? "Free" : String.format("%.2f lei", amenity.getPrice());
            model.addRow(new Object[]{amenity.getAmenityName(), price});
        }

        JTable amenitiesTable = new JTable(model);
        amenitiesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        amenitiesTable.setFillsViewportHeight(true);
        return amenitiesTable;
    }

    private JTextPane createSkiSlopesTextPane(List<SkiSlope> skiSlopes) {
        JTextPane skiSlopesTextPane = new JTextPane();
        skiSlopesTextPane.setContentType("text/html");
        skiSlopesTextPane.setEditable(false);
        skiSlopesTextPane.setText(getSkiSlopeInfoWithColors(skiSlopes));
        return skiSlopesTextPane;
    }

    private JTable createAccommodationsTable(int centerId, String distanceFilter) {
        // Define the table model with non-editable cells
        DefaultTableModel model = new DefaultTableModel(new String[]{"Name", "Category", "Distance"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // All cells are non-editable
                return false;
            }
        };

        // Fetch and populate the accommodations data
        AccommodationDAO accommodationDAO = new AccommodationDAO();
        List<Accommodation> accommodations = accommodationDAO.getAccommodationsBySkiCenterId(centerId);

        for (Accommodation accommodation : accommodations) {
            // Convert distance to meters for comparison
            double distance = accommodation.getDistanceFromSkiCenter();

            // Determine if this accommodation should be included based on the distance filter
            boolean include = switch (distanceFilter) {
                case "< 1000m" -> distance < 1000;
                case "< 5000m" -> distance < 5000;
                case "< 10000m" -> distance < 10000;
                default -> true; // "All distances" or unrecognized filter
            };

            if (include) {
                model.addRow(new Object[]{
                        accommodation.getName(),
                        accommodation.getCategory(),
                        String.format("%.0fm", distance) // Assuming the distance is in meters
                });
            }
        }

        // Create and return the accommodations table with the custom model
        JTable accommodationsTable = new JTable(model);
        accommodationsTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        accommodationsTable.setFillsViewportHeight(true);

        // Add a mouse listener to handle clicks on the accommodation options
        accommodationsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = accommodationsTable.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        // Open a new window with the details of the selected accommodation
                        Accommodation selectedAccommodation = accommodations.get(row);
                        displayAccommodationDetails(selectedAccommodation);
                    }
                }
            }
        });

        return accommodationsTable;
    }


    private void displayAccommodationDetails(Accommodation accommodation) {
        JFrame detailsFrame = new JFrame(accommodation.getName() + " - Details");
        detailsFrame.setSize(800, 600);
        detailsFrame.setLocationRelativeTo(null);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Accommodation details fields with a border
        addInfoField(detailsPanel, "Name:", accommodation.getName());
        addInfoFieldSeparator(detailsPanel); // Add a separator line
        addInfoField(detailsPanel, "Category:", accommodation.getCategory());
        addInfoFieldSeparator(detailsPanel); // Add a separator line
        addInfoField(detailsPanel, "Distance from center:", String.format("%.0fm", accommodation.getDistanceFromSkiCenter()));
        addInfoFieldSeparator(detailsPanel); // Add a separator line
        addInfoField(detailsPanel, "Website:", accommodation.getWebsite());
        addInfoFieldSeparator(detailsPanel); // Add a separator line
        addInfoField(detailsPanel, "Address:", accommodation.getAddress());

        double overallReview = calculateOverallReview(accommodation.getAccommodationId(), ReviewType.ACCOMMODATION);

        // Overall Review Rating Label
        JLabel overallReviewLabel = new JLabel("Overall Review: " + String.format("%.2f", overallReview));
        overallReviewLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(overallReviewLabel);

        // Amenities section with a border
        JPanel amenitiesPanel = new JPanel();
        amenitiesPanel.setLayout(new BorderLayout());
        amenitiesPanel.setBorder(BorderFactory.createTitledBorder("Amenities")); // Add a titled border

        JTable amenitiesTable = createAmenitiesTable(accommodation.getAccommodationId());
        JScrollPane amenitiesScrollPane = new JScrollPane(amenitiesTable);
        amenitiesPanel.add(amenitiesScrollPane, BorderLayout.CENTER);

        detailsPanel.add(amenitiesPanel);

        // Room Types section with a border
        JPanel roomTypesPanel = new JPanel();
        roomTypesPanel.setLayout(new BorderLayout());
        roomTypesPanel.setBorder(BorderFactory.createTitledBorder("Room Types"));

        JComboBox<String> peopleFilterCombo = new JComboBox<>();
        peopleFilterCombo.addItem("All");
        for (int i = 1; i <= 6; i++) { // Adjust the range as needed
            peopleFilterCombo.addItem(String.valueOf(i));
        }

        JPanel filterPanel = new JPanel(new FlowLayout());
        filterPanel.add(new JLabel("Filter by number of people:"));
        filterPanel.add(peopleFilterCombo);
        roomTypesPanel.add(filterPanel, BorderLayout.NORTH);

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        List<RoomType> roomTypesList = roomTypeDAO.getRoomTypesForAccommodation(accommodation.getAccommodationId());

        // Initially set the room types table without any filter
        JTable roomTypesTable = createRoomTypesTable(roomTypesList, -1);
        JScrollPane roomTypesScrollPane = new JScrollPane(roomTypesTable);
        roomTypesPanel.add(roomTypesScrollPane, BorderLayout.CENTER);

        peopleFilterCombo.addActionListener(e -> {
            String selected = (String) peopleFilterCombo.getSelectedItem();
            int selectedPeople;

            if ("All".equals(selected)) {
                selectedPeople = -1; // Indicates no filter
            } else {
                assert selected != null;
                selectedPeople = Integer.parseInt(selected); // Convert the selected string to an integer
            }

            JTable filteredTable = createRoomTypesTable(roomTypesList, selectedPeople);
            roomTypesScrollPane.setViewportView(filteredTable);
        });

        detailsPanel.add(roomTypesPanel);

        // Buttons panel with FlowLayout for horizontal alignment
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Modify the openWebsiteButton label
        JButton openWebsiteButton = new JButton("Open Website and Book Now");
        openWebsiteButton.addActionListener(e -> openWebpage(accommodation.getWebsite()));
        buttonsPanel.add(openWebsiteButton);

        // See Reviews button
        JButton reviewButton = new JButton("See Reviews");
        reviewButton.addActionListener(e -> openReviewsFrame(accommodation.getAccommodationId(), ReviewType.ACCOMMODATION));
        buttonsPanel.add(reviewButton);

        // Add the buttons panel to the details panel
        detailsPanel.add(buttonsPanel);

        // Set background color
        detailsPanel.setBackground(new Color(240, 240, 240));

        // Add the details panel to the frame
        detailsFrame.add(detailsPanel);

        detailsFrame.setVisible(true);
    }

    private void addInfoFieldSeparator(JPanel panel) {
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing as a separator
    }

    private void openReviewsFrame(int entityId, ReviewType entityType) {
        JFrame reviewsFrame = new JFrame("Reviews");
        reviewsFrame.setSize(800, 600);
        reviewsFrame.setLayout(new BorderLayout(10, 10));
        reviewsFrame.setLocationRelativeTo(null);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout()); // Use FlowLayout for filter options

        // Create a combo box to select the filter rating
        JLabel filterLabel = new JLabel("Filter by Rating:");
        JComboBox<String> ratingFilterCombo = new JComboBox<>(new String[]{"All", "1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars"});
        ratingFilterCombo.setSelectedIndex(0); // Default to "All" ratings
        filterPanel.add(filterLabel);
        filterPanel.add(ratingFilterCombo);

        // Filter button to apply the filter
        JButton filterButton = new JButton("Filter");
        filterPanel.add(filterButton);

        // Add an action listener to the filter button
        filterButton.addActionListener(e -> {
            int filterRating = ratingFilterCombo.getSelectedIndex();
            updateReviewsPanel(reviewsFrame, entityId, entityType, filterRating);
        });

        reviewsFrame.add(filterPanel, BorderLayout.NORTH);

        // Initial display with no filter
        updateReviewsPanel(reviewsFrame, entityId, entityType, -1);

        JButton addReviewButton = new JButton("Have you ever been here? Give us your opinion!");
        addReviewButton.setFont(new Font("Arial", Font.BOLD, 14));
        addReviewButton.setBackground(new Color(100, 149, 237));
        addReviewButton.setForeground(Color.WHITE);
        addReviewButton.addActionListener(e -> openReviewDialog(entityId, entityType));
        reviewsFrame.add(addReviewButton, BorderLayout.SOUTH);

        reviewsFrame.setVisible(true);
    }

    private void updateReviewsPanel(JFrame reviewsFrame, int entityId, ReviewType entityType, int filterRating) {
        Component[] components = reviewsFrame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                reviewsFrame.remove(component);
                break;
            }
        }

        JPanel reviewsPanel = new JPanel();
        reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS));
        reviewsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ReviewDAO reviewDAO = new ReviewDAO();
        List<Review> reviews = reviewDAO.getReviewsByEntityId(entityType, entityId);

        for (Review review : reviews) {
            if (filterRating == -1 || (filterRating == 0 && review.getRating() >= 0) || (filterRating > 0 && review.getRating() == filterRating)){
                String username = reviewDAO.getUsernameById(review.getUserId());
                JPanel reviewPanel = new JPanel();
                reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
                reviewPanel.setBorder(BorderFactory.createTitledBorder("Review by: " + (username != null ? username : "Unknown User")));

                // Convert the overall rating to stars with custom font and size
                String starRating = convertRatingToStars(review.getRating());
                JLabel ratingLabel = new JLabel("Overall Rating: " + starRating);
                Font starFont = new Font("SansSerif", Font.PLAIN, 20); // Custom font and size
                ratingLabel.setFont(starFont);
                reviewPanel.add(ratingLabel);

                addSpecificRatings(reviewPanel, review);

                JTextArea reviewText = new JTextArea(review.getReviewText());
                reviewText.setFont(new Font("SansSerif", Font.PLAIN, 12));
                reviewText.setWrapStyleWord(true);
                reviewText.setLineWrap(true);
                reviewText.setEditable(false);
                reviewText.setBackground(new Color(233, 236, 239));
                reviewPanel.add(reviewText);

                reviewsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between reviews
                reviewsPanel.add(reviewPanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(reviewsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        reviewsFrame.add(scrollPane, BorderLayout.CENTER);
        reviewsFrame.revalidate(); // Refresh the frame
        reviewsFrame.repaint(); // Repaint the frame
    }

    private String convertRatingToStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    private void addSpecificRatings(JPanel reviewPanel, Review review) {
        if (review.getReviewType() == ReviewType.SKI_CENTER) {
            if (review.getSlopeConditionsRating() != null) {
                reviewPanel.add(new JLabel("Slope Conditions: " + convertRatingToStars(review.getSlopeConditionsRating())));
            }
            if (review.getFacilitiesQualityRating() != null) {
                reviewPanel.add(new JLabel("Facilities Quality: " + convertRatingToStars(review.getFacilitiesQualityRating())));
            }
            if (review.getSceneryViewsRating() != null) {
                reviewPanel.add(new JLabel("Scenery Views: " + convertRatingToStars(review.getSceneryViewsRating())));
            }
        } else if (review.getReviewType() == ReviewType.ACCOMMODATION) {
            if (review.getRoomComfortRating() != null) {
                reviewPanel.add(new JLabel("Room Comfort: " + convertRatingToStars(review.getRoomComfortRating())));
            }
            if (review.getLocationConvenienceRating() != null) {
                reviewPanel.add(new JLabel("Location Convenience: " + convertRatingToStars(review.getLocationConvenienceRating())));
            }
            if (review.getStaffServiceRating() != null) {
                reviewPanel.add(new JLabel("Staff Service: " + convertRatingToStars(review.getStaffServiceRating())));
            }
        }
    }

    private void openReviewDialog(int entityId, ReviewType entityType) {
        JDialog reviewDialog = new JDialog();
        reviewDialog.setTitle("Submit Review");
        reviewDialog.setSize(400, 300);
        reviewDialog.setLayout(new BorderLayout());
        reviewDialog.setLocationRelativeTo(null);

        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        JTextField reviewTextField = new JTextField(20);
        JLabel reviewTextLabel = new JLabel("Your Review:");

        // Define the rating categories and their corresponding values
        String[] ratingCategories;
        if (entityType == ReviewType.SKI_CENTER) {
            ratingCategories = new String[]{"Overall Rating", "Scenery Views Rating", "Facilities Quality Rating", "Slope Conditions Rating"};
        } else if (entityType == ReviewType.ACCOMMODATION) {
            ratingCategories = new String[]{"Overall Rating", "Staff Service Rating", "Room Comfort Rating", "Location Convenience Rating"};
        } else {
            ratingCategories = new String[]{"Overall Rating"};
        }

        Map<String, JComboBox<Integer>> ratingComboBoxes = new HashMap<>();

        for (String category : ratingCategories) {
            JLabel ratingLabel = new JLabel(category + ":");
            JComboBox<Integer> ratingValueComboBox = new JComboBox<>();
            for (int i = 1; i <= 5; i++) {
                ratingValueComboBox.addItem(i);
            }
            reviewPanel.add(ratingLabel);
            reviewPanel.add(ratingValueComboBox);
            ratingComboBoxes.put(category, ratingValueComboBox);
        }

        reviewPanel.add(reviewTextLabel);
        reviewPanel.add(reviewTextField);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String reviewText = reviewTextField.getText();
            Map<String, Integer> ratings = new HashMap<>();

            // Get ratings for each category
            for (String category : ratingCategories) {
                JComboBox<Integer> ratingValueComboBox = ratingComboBoxes.get(category);
                if (ratingValueComboBox != null) {
                    int selectedRating = (Integer) ratingValueComboBox.getSelectedItem();
                    ratings.put(category, selectedRating);
                }
            }

            submitReview(entityId, entityType, reviewText, ratings, reviewDialog);
        });

        cancelButton.addActionListener(e -> reviewDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        reviewDialog.add(reviewPanel, BorderLayout.CENTER);
        reviewDialog.add(buttonPanel, BorderLayout.PAGE_END);
        reviewDialog.setVisible(true);
    }

    private void submitReview(int entityId, ReviewType entityType, String reviewText, Map<String, Integer> ratings, JDialog reviewDialog) {
        ReviewDAO reviewDAO = new ReviewDAO();

        // Handle success/failure and display messages
        boolean success;
        if (entityType == ReviewType.SKI_CENTER) {
            Review review = new Review(0,
                    currentUserId,
                    reviewText,
                    ratings.get("Overall Rating"),
                    entityType,
                    entityId, // Set skiCenterId or accommodationId based on entityType
                    0, // Set accommodationId or skiCenterId based on entityType
                    ratings.get("Room Comfort Rating"), // Set roomComfortRating based on entityType
                    ratings.get("Location Convenience Rating"), // Set locationConvenienceRating based on entityType
                    ratings.get("Staff Service Rating"), // Set staffServiceRating based on entityType
                    ratings.get("Slope Conditions Rating"), // Set slopeConditionsRating based on entityType
                    ratings.get("Facilities Quality Rating"), // Set facilitiesQualityRating based on entityType
                    ratings.get("Scenery Views Rating") // Set sceneryViewsRating based on entityType
            );
            success = reviewDAO.addSkiCenterReview(review);
        } else {
            Review review = new Review(0,
                    currentUserId,
                    reviewText,
                    ratings.get("Overall Rating"),
                    entityType,
                    0, // Set skiCenterId or accommodationId based on entityType
                    entityId, // Set accommodationId or skiCenterId based on entityType
                    ratings.get("Room Comfort Rating"), // Set roomComfortRating based on entityType
                    ratings.get("Location Convenience Rating"), // Set locationConvenienceRating based on entityType
                    ratings.get("Staff Service Rating"), // Set staffServiceRating based on entityType
                    ratings.get("Slope Conditions Rating"), // Set slopeConditionsRating based on entityType
                    ratings.get("Facilities Quality Rating"), // Set facilitiesQualityRating based on entityType
                    ratings.get("Scenery Views Rating") // Set sceneryViewsRating based on entityType
            );
            success = reviewDAO.addAccommodationReview(review);
        }

        if (success) {
            JOptionPane.showMessageDialog(reviewDialog, "Review submitted successfully.");
            System.out.println("Review submitted successfully.");
        } else {
            JOptionPane.showMessageDialog(reviewDialog, "Failed to submit review. Please try again.");
            System.out.println("Failed to submit review. Please try again.");
        }

        reviewDialog.dispose();
    }

    private JTable createRoomTypesTable(List<RoomType> roomTypesList, int peopleFilter) {
        String[] columnNames = {"Room Type", "Single Beds", "Double Beds", "Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (RoomType roomType : roomTypesList) {
            int capacity = roomType.getNumSingleBeds() + 2 * roomType.getNumDoubleBeds();
            if (peopleFilter == -1 || capacity == peopleFilter) {
                String price = roomType.getPrice() == null ? "Free" : String.format("$%.2f", roomType.getPrice());
                model.addRow(new Object[]{
                        roomType.getRoomTypeName(),
                        roomType.getNumSingleBeds(),
                        roomType.getNumDoubleBeds(),
                        price
                });
            }
        }
        JTable roomTypesTable = new JTable(model);
        roomTypesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        roomTypesTable.setFillsViewportHeight(true);
        return roomTypesTable;
    }

    private JTable createAmenitiesTable(int accommodationId) {
        DefaultTableModel amenitiesModel = new DefaultTableModel(new String[]{"Amenity", "Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        AmenityDAO amenityDAO = new AmenityDAO();
        List<Amenity> amenities = amenityDAO.getAmenitiesForAccommodation(accommodationId);
        for (Amenity amenity : amenities) {
            String price = amenity.isFree() ? "Free" : String.format("$%.2f", amenity.getPrice());
            amenitiesModel.addRow(new Object[]{amenity.getAmenityName(), price});
        }

        JTable amenitiesTable = new JTable(amenitiesModel);
        amenitiesTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        amenitiesTable.setFillsViewportHeight(true);
        return amenitiesTable;
    }

    private void openWebpage(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                JOptionPane.showMessageDialog(null, "Unable to open the website. Please check your Internet connection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


    private void addInfoField(JPanel panel, String label, String value) {
        JLabel infoLabel = new JLabel(label);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);

        JTextField infoField = new JTextField(value);
        infoField.setEditable(false);
        infoField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoField);
    }

    private String getSkiSlopeInfoWithColors(List<SkiSlope> skiSlopes) {
        StringBuilder htmlContent = new StringBuilder("<html><body>");
        for (SkiSlope slope : skiSlopes) {
            String color = getDifficultyLevelColor(slope.getDifficulty());
            int lengthWithoutDecimal = (int) slope.getLength().doubleValue();
            htmlContent.append(String.format(
                    "<p>Slope Name: %s<br>Difficulty Level: <span style='color:%s; font-weight:bold;'>%s</span><br>Length: %d meters<br>Lift: %s</p>",
                    slope.getName(), color, slope.getDifficulty(), lengthWithoutDecimal, slope.getLift()));
        }
        htmlContent.append("</body></html>");
        return htmlContent.toString();
    }

    private String getDifficultyLevelColor(String difficultyLevel) {
        switch (difficultyLevel.toLowerCase()) {
            case "beginner":
                return "#0000FF"; // Blue using hex code
            case "intermediate":
                return "#FF0000"; // Red using hex code
            case "expert":
                return "#000000"; // Black using hex code
            default:
                return "#000000"; // Default to black
        }
    }

    private String mapSearchTypeToColumn(String searchType) {
        switch (searchType) {
            case "Name":
                return "name";
            case "City":
                return "city";
            case "County":
                return "county";
            case "Region":
                return "region";
            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }
}