import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faRunning } from "@fortawesome/free-solid-svg-icons";
import { useAppSelector } from '../../store/hooks';
import ThemeSwitcher from "../theme/ThemeSwitcher";
import { selectSessionCount, selectCompletedBatches } from "../../store/slices/sessionSlice";

const Header = () => {
  const sessionCount = useAppSelector(selectSessionCount);
  const completedBatches = useAppSelector(selectCompletedBatches);

  return (
      <header className="bg-card-light dark:bg-card-dark p-4 shadow-light dark:shadow-dark mb-8 flex justify-between items-center">
        <div className="flex items-center gap-6">
          <h1>Buffer Memory Monitor</h1>
          <div className="text-sm text-light-secondary dark:text-dark-secondary bg-light dark:bg-dark py-2 px-4 rounded-sm shadow-light dark:shadow-dark flex gap-4">
          <span className="flex items-center gap-2" title="Active viewers">
            <FontAwesomeIcon icon={faEye} className="text-accent dark:text-accent-dark" />
            <span>{sessionCount}</span>
          </span>
            <span className="flex items-center gap-2" title="Completed Batches">
            <FontAwesomeIcon icon={faRunning} className="text-accent dark:text-accent-dark" />
            <span>{completedBatches}</span>
          </span>
          </div>
        </div>
        <ThemeSwitcher />
      </header>
  );
};

export default Header;
