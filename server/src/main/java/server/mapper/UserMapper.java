package server.mapper;


import server.dto.UserDTO;
import server.model.User;

public class UserMapper {
    public static UserDTO toDTO(User user)
    {
        return user == null ? null : new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
    public static User fromDTO(UserDTO dto)
    {
        return dto != null ? null : new User(
                dto.getUserId(),
                dto.getUsername(),
                null,
                dto.getEmail(),
                dto.getCreatedAt()
        );
    }
}
