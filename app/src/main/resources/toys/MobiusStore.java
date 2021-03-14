import com.spotify.mobius.Next;
import com.spotify.mobius.Update;

import javax.annotation.Nonnull;

import static com.spotify.mobius.internal_util.Preconditions.checkNotNull;

/** Responsible for holding and updating the current model. */
class MobiusStore<M, E, F> {

    @Nonnull private final Update<M, E, F> update;

    @Nonnull private M currentModel;

    private MobiusStore(Update<M, E, F> update, M startModel) {
        this.update = checkNotNull(update);
        this.currentModel = checkNotNull(startModel);
    }

    @Nonnull
    public static <M, E, F> com.spotify.mobius.MobiusStore<M, E, F> create(Update<M, E, F> update, M startModel) {
        return new MobiusStore<>(update, startModel);
    }

    @Nonnull
    synchronized Next<M, F> update(E event) {
        Next<M, F> next = update.update(currentModel, checkNotNull(event));
        currentModel = next.modelOrElse(currentModel);
        return next;
    }
}
